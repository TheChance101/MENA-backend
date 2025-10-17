package net.thechance.chat.service

import jakarta.persistence.EntityManager
import net.thechance.chat.entity.*
import net.thechance.chat.repository.ChatRepository
import net.thechance.chat.repository.MessageRepository
import net.thechance.chat.service.exception.NotFoundException
import net.thechance.chat.service.model.ChatModel
import net.thechance.chat.service.model.MessageImageRequestArgs
import net.thechance.chat.service.model.MessageRequestArgs
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.util.*

@Service
class ChatService(
    private val messageRepository: MessageRepository,
    private val chatRepository: ChatRepository,
    private val contactUserService: ContactUserService,
    private val attachmentStorageService: AttachmentStorageService,
    private val contactService: ContactService,
    private val entityManager: EntityManager
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
        args.messageId?.let { messageId ->
            messageRepository.findById(messageId).orElse(null)?.let { return it }
        }
        val chat = entityManager.getReference(Chat::class.java, args.chatId)
        val message = Message(
            id = UUID.randomUUID(),
            senderId = args.senderId,
            chat = chat,
            text = args.text,
            sentAt = Instant.now(),
        )

        return messageRepository.save(message)
    }

    @Transactional
    fun saveMessageImage(args: MessageImageRequestArgs): Message {
        val message = saveMessage(MessageRequestArgs(args.chatId, args.senderId, null, args.messageId))
        val imageUrl = attachmentStorageService.uploadImage(
            file = args.image,
            fileName = args.image.originalFilename ?: "${message.id}-Untitled",
            folderName = FOLDER_NAME
        )
        val updatedMessage = message.copy(
            images = message.images.toMutableList().apply {
                add(imageUrl)
            }
        )
        return messageRepository.save(updatedMessage)
    }

    fun getAllChatMessages(chatId: UUID, pageable: Pageable) =
        messageRepository.getAllByChatIdOrderBySentAtDesc(chatId, pageable)


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
                lastMessages.firstOrNull { it.chat.id == chat.id },
                unreadCounts.firstOrNull { it.chatId == chat.id }?.unreadCount ?: 0
            )
        }
        return chatsSummaries
    }

    fun getUserChatSummaryById(chatId: UUID, userId: UUID): ChatSummary {
        val chat = chatRepository.findByIdOrNull(chatId) ?: throw NotFoundException("no chat was found with id: $chatId")
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
        val chat = chatRepository.findByIdOrNull(chatId) ?: throw NotFoundException("no chat was found with id: $chatId")
        val otherUser = chat.users.firstOrNull { it.id != userId }
        val contact =otherUser?.let{
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
        val chat = chatRepository.findByIdOrNull(chatId) ?: throw NotFoundException("no chat was found with id: $chatId")
        return chat.users.map { it.id }
    }

    private fun getChatName(contact: Contact?, user: ContactUser? ): String{
        return contact?.let { "${it.firstName} ${it.lastName}" }
            ?: user?.let { "${it.firstName} ${it.lastName}" }.orEmpty()
    }


    companion object {
        private const val FOLDER_NAME = "chat_attachments"
    }
}