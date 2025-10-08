package net.thechance.chat.service

import jakarta.persistence.EntityManager
import net.thechance.chat.api.dto.ChatsResponse
import net.thechance.chat.api.dto.toResponse
import net.thechance.chat.entity.Chat
import net.thechance.chat.entity.Message
import net.thechance.chat.repository.ChatRepository
import net.thechance.chat.repository.MessageRepository
import net.thechance.chat.service.args.CreateMessageArgs
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class ChatService(
    private val messageRepository: MessageRepository,
    private val chatRepository: ChatRepository,
    private val contactUserService: ContactUserService,
    private val entityManager: EntityManager,
    private val contactService: ContactService,
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

    fun saveMessage(message: CreateMessageArgs) {
        val chat = entityManager.getReference(Chat::class.java, message.chatId)
        messageRepository.save(
            Message(
                id = message.id,
                senderId = message.senderId,
                chat = chat,
                text = message.text,
                sentAt = message.sendAt,
            )
        )
    }

    fun getAllChatMessages(chatId: UUID, pageable: Pageable) =
        messageRepository.getAllByChatIdOrderBySentAt(chatId, pageable)


    fun markChatMessagesAsRead(chatId: UUID, userId: UUID) =
        messageRepository.updateIsReadByChatIdAndSenderIdNot(chatId = chatId, userId = userId)

    fun getUserChats(userId: UUID, pageable: Pageable): Page<ChatsResponse> {
        val chats = chatRepository.findAllByUserId(userId, pageable)
        val chatResponses = chats.map { chat ->
            val lastMessage = messageRepository.findTopByChatIdOrderBySentAtDesc(chat.id)
            val unreadCount = messageRepository.countByChatIdAndSenderIdNotAndIsReadFalse(chat.id, userId)
            val contact = chat.users.firstOrNull { it.id != userId }?.let { requester ->
                contactService.getContactByOwnerIdAndContactUserId(userId, requester.id)
            }

            chat.toResponse(
                requesterId = userId,
                contact = contact,
                message = lastMessage,
                unreadCount = unreadCount
            )
        }
        return PageImpl(chatResponses, pageable, chatRepository.countByUserId(userId))

    }
}