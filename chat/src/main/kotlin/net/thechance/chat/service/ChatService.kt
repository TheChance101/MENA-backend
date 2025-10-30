package net.thechance.chat.service

import net.thechance.chat.entity.*
import net.thechance.chat.repository.ChatRepository
import net.thechance.chat.repository.MessageReactionRepository
import net.thechance.chat.repository.MessageRepository
import net.thechance.chat.service.exception.NotFoundException
import net.thechance.chat.service.model.ChatModel
import net.thechance.chat.service.model.MessageImageRequestArgs
import net.thechance.chat.service.model.MessageReactionRequestArgs
import net.thechance.chat.service.model.MessageRequestArgs
import net.thechance.chat.service.model.MessageWithReactions
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.text.Normalizer
import java.time.Instant
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
    fun getOrCreateConversationByParticipants(userId: UUID, receiverId: UUID): Chat {
        val users = setOf(userId, receiverId)

        val existingChat = chatRepository.findByUsersIds(users)
        if (existingChat != null) return existingChat

        val requester = contactUserService.getUserById(userId)
        val otherUser = contactUserService.getUserById(receiverId)

        return chatRepository.save(Chat(users = mutableSetOf(requester, otherUser)))
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
            fileName = args.image.originalFilename ?: "${Instant.now()}-Untitled",
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

    fun addReaction(args: MessageReactionRequestArgs): MessageReaction {
        messageRepository.findByIdOrNull(args.messageId)
            ?: throw NotFoundException("no message was found with id: ${args.messageId}")

        if (!isValidEmoji(args.emoji)) {
            throw IllegalArgumentException("Invalid emoji")
        }

        val existing = messageReactionRepository.findByMessageIdAndUserId(args.messageId, args.userId)
        return existing?.copy(emoji = args.emoji)?.let { messageReactionRepository.save(it) }
            ?: messageReactionRepository.save(MessageReaction(messageId = args.messageId, userId = args.userId, emoji = args.emoji))
    }

    fun deleteReaction(messageId: UUID, userId: UUID) {
        messageReactionRepository.deleteByMessageIdAndUserId(messageId, userId)
    }

    private fun isValidEmoji(input: String): Boolean {
        val emojiRange = """(?:\u00A9|\u00AE|[\u2000-\u206F]|[\u2190-\u2BFF]|[\u2E00-\u2E7F]|[\u2300-\u23FF]|[\u24C2-\u1F251]|\u00A9|\u00AE|\u203C|\u2049|[\u2122\u2139]|\u{1F3F4}[\u{E006E}-\u{E007A}]+|\u{1F3F4}[\u{E006E}-\u{E007A}]*\u{E007F}|[\u{1F1E6}-\u{1F1FF}]{2}|[\u{1F3F4}\u{E006E}-\u{E007A}]+|\u{1F3F4}\u{E007F}|\u{1F3F3}\u{1F3F4}|[\u{1F6F7}-\u{1F6F8}\u{1F3FB}-\u{1F3FF}]|[\u{1F1E6}-\u{1F1FF}][\u{1F1E6}-\u{1F1FF}]|[\u{1F1F2}-\u{1F1F4}\u{1F1E6}-\u{1F1FF}\u{1F1F2}-\u{1F1F4}]|[\u{1F1E6}-\u{1F1FF}\u{1F1E6}-\u{1F1FF}]|[\u{1F30D}\u{E0067}-\u{E007F}]|[\u26F9\u2708-\u270D\u26FD]|[\u{1F3C2}-\u{1F3C4}\u{1F3FB}-\u{1F3FF}]|[\u{1F3CA}-\u{1F3CB}\u{1F3FB}-\u{1F3FF}]|[\u{1F680}-\u{1F6C5}\u{1F3FB}-\u{1F3FF}]|[\u{1F693}-\u{1F6A5}\u{1F3FB}-\u{1F3FF}]|[\u{1F6B2}\u{1F3FB}-\u{1F3FF}]|[\u{1F6C0}\u{1F3FB}-\u{1F3FF}]|[\u{1F6CC}\u{1F3FB}-\u{1F3FF}]|[\u{1F6F4}\u{1F3FB}-\u{1F3FF}]|[\u{1F6F9}\u{1F3FB}-\u{1F3FF}]|[\u{1F918}-\u{1F919}\u{1F3FB}-\u{1F3FF}]|[\u{1F93E}\u{1F3FB}-\u{1F3FF}]|[\u{1F9D1}-\u{1F9DD}\u{1F3FB}-\u{1F3FF}]|[\u{1F9DE}\u{1F3FB}-\u{1F3FF}]|[\u{1F9DF}\u{1F3FB}-\u{1F3FF}]|[\u{1FAF1}\u{1F3FB}-\u{1F3FF}]|[\u{1F9E6}\u{1F3FB}-\u{1F3FF}])"""
        val emojiRegex = Regex("^$emojiRange$", RegexOption.MULTILINE)

        val normalized = Normalizer.normalize(input.trim(), Normalizer.Form.NFC)
        if (normalized.isEmpty()) return false
        return emojiRegex.matches(normalized)
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


    fun markChatMessagesAsRead(chatId: UUID, userId: UUID) =
        messageRepository.updateIsReadByChatIdAndSenderIdNot(chatId = chatId, userId = userId)

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
        val otherUser = chat.users.firstOrNull { it.id != userId }
        val contact = otherUser?.let {
            contactService.getContactByOwnerIdAndContactUserId(userId, it.id)
        }
        return ChatModel(
            name = getChatName(contact, otherUser),
            imageUrl = otherUser?.imageUrl,
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