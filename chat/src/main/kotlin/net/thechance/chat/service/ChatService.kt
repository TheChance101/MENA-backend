package net.thechance.chat.service

import net.thechance.chat.api.dto.MessageDto
import net.thechance.chat.entity.Chat
import net.thechance.chat.entity.Message
import net.thechance.chat.repository.ChatRepository
import net.thechance.chat.repository.ContactUserRepository
import net.thechance.chat.repository.MessageRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class ChatService(
    private val messageRepository: MessageRepository,
    private val chatRepository: ChatRepository,
    private val contactUserRepository: ContactUserRepository
) {
    @Transactional
    fun getOrCreateConversationByParticipants(userId: UUID, receiverId: UUID): Chat {
        val userIds = setOf(userId, receiverId)

        val existingChat = chatRepository.findPrivateChatBetweenUsers(userIds)
        if (existingChat != null) return existingChat

        val requester = contactUserRepository.findById(userId)
            .orElseThrow { IllegalArgumentException("Requester not found") }
        val otherUser = contactUserRepository.findById(receiverId)
            .orElseThrow { IllegalArgumentException("Other user not found") }

        return chatRepository.save(Chat(users = mutableSetOf(requester, otherUser)))
    }

    fun saveMessage(message: MessageDto) {
        chatRepository.findByIdOrNull(message.chatId)?.let { chat ->
            messageRepository.save(
                Message(
                    id = message.id,
                    senderId = message.senderId,
                    chat = chat,
                    text = message.text,
                    sentAt = message.sendAt,
                )
            )
        } ?: throw IllegalArgumentException("Chat with id ${message.chatId} not found")
    }

    fun getAllChatMessages(
        chatId: UUID,
        pageable: Pageable,
    ): Page<Message> {
        return if (pageable.pageNumber <= 0 || pageable.pageSize <= 0) {
            messageRepository.getAllByChatIdOrderBySentAt(chatId, Pageable.unpaged())
        } else {
            messageRepository.getAllByChatIdOrderBySentAt(
                chatId,
                PageRequest.of(pageable.pageNumber - 1, pageable.pageSize)
            )
        }
    }

    fun markChatMessagesAsRead(chatId: UUID, userId: UUID) {
        messageRepository.updateIsReadByChatIdAndSenderIdNot(chatId = chatId, userId = userId)
    }
}