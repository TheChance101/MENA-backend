package net.thechance.chat.service

import jakarta.persistence.EntityManager
import net.thechance.chat.entity.Chat
import net.thechance.chat.entity.ChatSummary
import net.thechance.chat.entity.Contact
import net.thechance.chat.entity.ContactUser
import net.thechance.chat.entity.Message
import net.thechance.chat.repository.ChatRepository
import net.thechance.chat.repository.MessageRepository
import net.thechance.chat.service.args.CreateMessageArgs
import org.springframework.data.domain.Page
import net.thechance.chat.service.exception.NotFoundException
import net.thechance.chat.service.model.ChatModel
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
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
        val unreadCounts = chatRepository.findUnreadCountsForChats(chats.content.map { it.id })

        val chatSummaries = chats.map { chat ->
            val otherUser = chat.users.firstOrNull { it.id != userId }
            chat.toSummary(
                userId,
                otherUser,
                lastMessages.firstOrNull { it.chat.id == chat.id },
                unreadCounts.firstOrNull { it.chatId == chat.id }?.unreadCount ?: 0
            )
        }
        return chatSummaries
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

    private fun getChatName(contact: Contact?, user: ContactUser? ): String{
        return contact?.let { "${it.firstName} ${it.lastName}" }
            ?: user?.let { "${it.firstName} ${it.lastName}" }.orEmpty()
    }

}