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

                return messageRepository.save(message)
            }
        } ?: throw IllegalArgumentException("Chat with id ${chatMessage.chatId} not found")
    }

/*    @Transactional
    fun getOrCreateConversationByParticipants(requesterId: UUID, theOtherUserId: UUID): ChatModel {
        val requester = contactUserRepository.findById(requesterId)
            .orElseThrow { IllegalArgumentException("Requester with id $requesterId not found") }
        val theOtherUser = contactUserRepository.findById(theOtherUserId)
            .orElseThrow { IllegalArgumentException("User with id $theOtherUserId not found") }

        chatRepository.findByUsersIdsAndGroupChatIsNull(setOf(requester.id, theOtherUser.id))
            ?.let { return it.toModel(requesterId) }

        val savedConversation = chatRepository.save(Chat(users = mutableSetOf(requester, theOtherUser)))

        return chatRepository.findByIdIs(savedConversation.id)?.toModel(requesterId)
            ?: throw IllegalStateException("Failed to retrieve the newly created conversation.")
    }*/

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

    fun markChatMessagesAsRead(chatId: UUID, userId: UUID) {
        messageRepository.updateIsReadByChatIdAndSenderIdNot(chatId = chatId, userId = userId)
    }
}