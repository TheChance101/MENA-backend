package net.thechance.chat.service

import net.thechance.chat.api.dto.MessageDto
import net.thechance.chat.entity.Chat
import net.thechance.chat.entity.Message
import net.thechance.chat.repository.ChatRepository
import net.thechance.chat.repository.ContactUserRepository
import net.thechance.chat.repository.MessageRepository
import net.thechance.chat.service.model.ChatModel
import net.thechance.chat.service.model.toModel
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
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
    fun getOrCreateConversationByParticipants(userId: UUID, receiverId: UUID): ChatModel {
        val userIds = setOf(userId, receiverId)

        val existingChat = chatRepository.findPrivateChatBetweenUsers(userIds, userIds.size.toLong())
        if (existingChat != null) {
            return existingChat.toModel(userId)
        }

        val requester = contactUserRepository.findById(userId)
            .orElseThrow { IllegalArgumentException("Requester not found") }
        val otherUser = contactUserRepository.findById(receiverId)
            .orElseThrow { IllegalArgumentException("Other user not found") }

        val newChat = chatRepository.save(Chat(users = mutableSetOf(requester, otherUser)))
        return newChat.toModel(userId)
    }

    fun saveMessage(message: MessageDto) {
        chatRepository.findByIdIs(message.chatId)?.let { chat ->
            Message(
                id = message.id,
                senderId = message.senderId,
                chat = chat,
                text = message.text,
                sendAt = message.sendAt
            ).let { message ->
                messageRepository.save(message)
            }
        } ?: throw IllegalArgumentException("Chat with id ${message.chatId} not found")
    }

    fun getAllChatMessages(
        chatId: UUID,
        pageable: Pageable,
    ): Page<Message> {
        return if (pageable.pageNumber <= 0 || pageable.pageSize <= 0) {
            messageRepository.getAllByChatIdOrderBySendAt(chatId, Pageable.unpaged())
        } else {
            messageRepository.getAllByChatIdOrderBySendAt(
                chatId,
                PageRequest.of(pageable.pageNumber - 1, pageable.pageSize)
            )
        }
    }

    fun markChatMessagesAsRead(chatId: UUID, userId: UUID) {
        messageRepository.updateIsReadByChatIdAndSenderIdNot(chatId = chatId, userId = userId)
    }
}