package net.thechance.chat.service

import net.thechance.chat.entity.Chat
import net.thechance.chat.entity.ContactUser
import net.thechance.chat.entity.Message
import net.thechance.chat.repository.ChatRepository
import net.thechance.chat.repository.ContactUserRepository
import net.thechance.chat.repository.MessageRepository
import net.thechance.chat.service.model.ChatModel
import net.thechance.chat.service.model.MessageModel
import net.thechance.chat.service.model.toModel
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class ChatService(
    private val messageRepository: MessageRepository,
    private val chatRepository: ChatRepository,
    private val contactUserRepository: ContactUserRepository
) {

    fun saveMessage(chatMessage: MessageModel): Message {
        chatRepository.findByIdIs(chatMessage.chatId)?.let { chat ->
            Message(
                id = chatMessage.id,
                senderId = chatMessage.senderId,
                chat = chat,
                text = chatMessage.text,
                sendAt = chatMessage.sendAt
            ).let { message ->
                chat.messages.add(message)
                return messageRepository.save(message)
            }
        } ?: throw IllegalArgumentException("Chat with id ${chatMessage.chatId} not found")
    }

    @Transactional
    fun getOrCreateConversationByParticipants(requesterId: UUID, theOtherUserId: UUID): ChatModel {
        val requester = contactUserRepository.findById(requesterId)
            .orElseThrow { IllegalArgumentException("Requester with id $requesterId not found") }
        val theOtherUser = contactUserRepository.findById(theOtherUserId)
            .orElseThrow { IllegalArgumentException("User with id $theOtherUserId not found") }

        chatRepository.findByUsersIdIn(listOf(requesterId, theOtherUserId).toSet())
            ?.let { return it.toModel(requesterId) }

        val savedConversation = chatRepository.save(Chat(users = mutableSetOf(requester, theOtherUser)))

        return chatRepository.findByIdIs(savedConversation.id)?.toModel(requesterId)
            ?: throw IllegalStateException("Failed to retrieve the newly created conversation.")
    }

    @Transactional
    fun markChatMessagesAsRead(chatId: UUID, user: ContactUser) {
        var page = 0
        val pageSize = PAGE_SIZE
        var messages: List<Message>
        do {
            messages = messageRepository.findAllByChatIdAndReadByUsersNotContaining(
                chatId,
                user,
                Pageable.ofSize(pageSize).withPage(page)
            )
            if (messages.isNotEmpty()) {
                messageRepository.saveAll(messages.onEach { it.readByUsers += user })
            }
            page++
        } while (messages.size == pageSize)
    }

    companion object {
        const val PAGE_SIZE = 500
    }
}