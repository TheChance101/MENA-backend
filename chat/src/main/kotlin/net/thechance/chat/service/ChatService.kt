package net.thechance.chat.service

import net.thechance.chat.entity.*
import net.thechance.chat.repository.ChatRepository
import net.thechance.chat.repository.MessageReactionRepository
import net.thechance.chat.repository.MessageRepository
import net.thechance.chat.service.exception.NotFoundException
import net.thechance.chat.service.model.*
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class ChatService(
    private val messageRepository: MessageRepository,
    private val messageReactionRepository: MessageReactionRepository,
    private val chatRepository: ChatRepository,
    private val contactUserService: ContactUserService,
    private val attachmentStorageService: AttachmentStorageService,
    private val contactService: ContactService
) {
    @Transactional
    fun getChatByUserIds(userId: UUID, receiverId: UUID): ChatModel {
        val usersId = setOf(userId, receiverId)
        val requester = contactUserService.getUserById(userId)
        val otherUser = contactUserService.getUserById(receiverId)
        val contact = contactService.getContactByOwnerIdAndContactUserId(userId, otherUser.id)

        val chatName = getChatName(contact, otherUser)
        val imageUrl = otherUser.imageUrl.orEmpty()

        val chat = findOrCreateChat(
            usersId = usersId,
            requester = requester,
            otherUser = otherUser
        ).toModel(
            chatName = chatName,
            imageUrl = imageUrl,
            requesterId = userId
        )
        return chat
    }

    private fun findOrCreateChat(usersId: Set<UUID>, requester: ContactUser, otherUser: ContactUser): Chat {
        return chatRepository.findByUsersIds(usersId)
            ?: chatRepository.save(Chat(users = mutableSetOf(requester, otherUser)))
    }

    @Transactional
    fun saveMessage(args: MessageRequestArgs): Message {
        return messageRepository.save(
            Message(
                senderId = args.senderId,
                chatId = args.chatId,
                text = args.text,
            )
        )
    }

    @Transactional
    fun saveMessageImage(args: MessageImageRequestArgs): Message {
        val imageUrl = attachmentStorageService.uploadImage(
            file = args.image,
            folderName = FOLDER_NAME
        )

        return messageRepository.save(
            Message(
                senderId = args.senderId,
                chatId = args.chatId,
                imageUrl = imageUrl
            )
        )
    }

    fun getMessageById(messageId: UUID): Message {
        return messageRepository.findByIdOrNull(messageId)
            ?: throw NotFoundException("no message was found with id: $messageId")
    }

    fun addReaction(args: MessageReactionRequestArgs): MessageReaction {
        val existing = messageReactionRepository.findByMessageIdAndUserId(args.messageId, args.userId)

        return existing?.copy(emoji = args.emoji)?.let { messageReactionRepository.save(it) }
            ?: messageReactionRepository.save(
                MessageReaction(
                    messageId = args.messageId,
                    userId = args.userId,
                    emoji = args.emoji
                )
            )
    }

    fun deleteReaction(args: MessageReactionRequestArgs): MessageReaction {
        return messageReactionRepository.deleteByMessageIdAndUserId(args.messageId, args.userId)
            ?: throw NotFoundException("no message reactions was found")
    }

    fun getAllChatMessages(chatId: UUID, pageable: Pageable) =
        messageRepository.getAllByChatIdOrderBySentAtDesc(chatId, pageable)

    fun getAllChatMessagesWithReactions(chatId: UUID, pageable: Pageable): Page<MessageWithReactions> {
        val messagesPage = messageRepository.getAllByChatIdOrderBySentAtDesc(chatId, pageable)
        if (messagesPage.isEmpty) return Page.empty(pageable)

        val messageIds = messagesPage.content.map { it.id }
        val reactions = messageReactionRepository.findByMessageIdIn(messageIds)
        val reactionsByMessageId = reactions.groupBy { it.messageId }

        return messagesPage.map { message ->
            val reactionsForMessage = reactionsByMessageId[message.id] ?: emptyList()
            MessageWithReactions(message, reactionsForMessage)
        }
    }

    fun markChatMessagesAsRead(chatId: UUID, userId: UUID) {
        messageRepository.updateIsReadByChatIdAndSenderIdNot(chatId = chatId, userId = userId)
    }

    fun getUserChatsSummaries(userId: UUID, pageable: Pageable): Page<ChatSummary> {
        val chats = chatRepository.findAllByUserId(userId, pageable)
        val chatIds = chats.content.map { it.id }

        val lastMessages = messageRepository.findLastMessagesForChats(chatIds)
        val unreadCounts = chatRepository.findUnreadCountsForChats(chatIds)

        val chatsSummaries = chats.map { chat ->
            val otherUser = chat.users.firstOrNull { it.id != userId }
            chat.toSummary(
                userId,
                otherUser,
                lastMessages.firstOrNull { it.chatId == chat.id },
                unreadCounts.firstOrNull { it.chatId == chat.id }?.unreadCount ?: 0
            )
        }
        return chatsSummaries
    }

    fun getUserChatSummaryById(chatId: UUID, userId: UUID): ChatSummary {
        val chat =
            chatRepository.findByIdOrNull(chatId) ?: throw NotFoundException("no chat was found with id: $chatId")
        val otherUser = chat.users.firstOrNull { it.id != userId }
        val lastMessage = messageRepository.findTopByChatIdOrderBySentAtDesc(chatId)
        val unreadCount = chatRepository.findUnreadCountsForChats(listOf(chatId)).firstOrNull()?.unreadCount ?: 0
        return chat.toSummary(
            userId,
            otherUser,
            lastMessage,
            unreadCount
        )
    }

    fun getChatById(chatId: UUID, userId: UUID): ChatModel {
        val chat =
            chatRepository.findByIdOrNull(chatId) ?: throw NotFoundException("no chat was found with id: $chatId")
        val otherUser = chat.users.first { it.id != userId }
        val contact = otherUser.let {
            contactService.getContactByOwnerIdAndContactUserId(userId, it.id)
        }
        return ChatModel(
            name = getChatName(contact, otherUser),
            imageUrl = otherUser.imageUrl,
            requesterId = userId,
            id = chatId
        )
    }

    fun getChatUsersIds(chatId: UUID): List<UUID> {
        val chat =
            chatRepository.findByIdOrNull(chatId) ?: throw NotFoundException("no chat was found with id: $chatId")
        return chat.users.map { it.id }
    }

    private fun getChatName(contact: Contact?, user: ContactUser?): String {
        return contact?.let { "${it.firstName} ${it.lastName}" }
            ?: user?.let { "${it.firstName} ${it.lastName}" }.orEmpty()
    }


    companion object {
        private const val FOLDER_NAME = "chat_attachments"
    }
}