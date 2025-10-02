package net.thechance.chat.service

import net.thechance.chat.api.dto.MessageDto
import net.thechance.chat.entity.Chat
import net.thechance.chat.entity.Message
import net.thechance.chat.repository.ChatRepository
import net.thechance.chat.repository.ContactUserRepository
import net.thechance.chat.repository.MessageRepository
import net.thechance.chat.service.args.CreateMessageArgs
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class ChatService(
    private val messageRepository: MessageRepository,
    private val chatRepository: ChatRepository,
    private val contactUserRepository: ContactUserRepository,
    private val contactUserService: ContactUserService,
) {
    @Transactional
    fun getOrCreateConversationByParticipants(userId: UUID, receiverId: UUID): Chat {
        val users = setOf(
            contactUserRepository.getReferenceById(userId),
            contactUserRepository.getReferenceById(receiverId)
        )

        val existingChat = chatRepository.findByUsers(users)
        if (existingChat != null) return existingChat

        val requester = contactUserService.getUserById(userId)
        val otherUser = contactUserService.getUserById(receiverId)

        return chatRepository.save(Chat(users = mutableSetOf(requester, otherUser)))
    }

    fun saveMessage(message: CreateMessageArgs) {
        val chat = chatRepository.getReferenceById(message.chatId)
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
}