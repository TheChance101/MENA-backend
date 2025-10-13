package net.thechance.chat.service

import jakarta.persistence.EntityManager
import net.thechance.chat.entity.Chat
import net.thechance.chat.entity.ChatSummary
import net.thechance.chat.entity.Message
import net.thechance.chat.repository.ChatRepository
import net.thechance.chat.repository.MessageRepository
import net.thechance.chat.service.args.CreateMessageArgs
import org.springframework.data.domain.Page
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

    fun getUserChats(userId: UUID, pageable: Pageable): Page<ChatSummary> {
        val chats = chatRepository.findAllByUserId(userId, pageable)
        val lastMessages = messageRepository.findLastMessagesForChats(chats.content.map { it.id })
        val unreadCounts = chatRepository.findUnreadCountsForChats(
            chatIds = chats.content.map { it.id }
        ).associate { it.entries.first().toPair() }

        val chatSummaries = chats.map { chat ->
            messageRepository.findTopByChatIdOrderBySentAtDesc(chat.id).also { println("$it") }
            val otherUser = chat.users.firstOrNull { it.id != userId }
            chat.toSummary(userId, otherUser, lastMessages.firstOrNull { it.chat.id == chat.id }, unreadCounts[chat.id] ?: 0)
        }
        return chatSummaries
    }
}