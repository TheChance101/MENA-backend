package net.thechance.chat.service

import net.thechance.chat.entity.Conversation
import net.thechance.chat.entity.ConversationParticipants
import net.thechance.chat.entity.ConversationParticipantsId
import net.thechance.chat.entity.Message
import net.thechance.chat.repository.ConversationRepository
import net.thechance.chat.repository.ConversationParticipantsRepository
import net.thechance.chat.repository.MessageRepository
import net.thechance.chat.service.model.ConversationModel
import net.thechance.chat.service.model.MessageModel
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class ChatService(
    private val messageRepository: MessageRepository,
    private val conversationRepository: ConversationRepository,
    private val conversationParticipantsRepository: ConversationParticipantsRepository
) {

    fun saveMessage(chatMessage: MessageModel): Message {
        val messageEntity = Message(
            id = chatMessage.id,
            senderId = chatMessage.senderId,
            conversationId = chatMessage.conversationId,
            text = chatMessage.text,
            sendAt = chatMessage.sendAt,
            //    isRead = chatMessage.isRead
        )

        return messageRepository.save(messageEntity)

    }

    @Transactional
    fun getOrCreateConversationByParticipants(requesterId: UUID, theOtherUserId: UUID): ConversationModel {
        conversationRepository.getConversationByParticipants(listOf(requesterId, theOtherUserId))
            ?.let { return it }

        val savedConversation = conversationRepository.save(Conversation(isGroup = false))

        val participants = listOf(requesterId, theOtherUserId).map { userId ->
            ConversationParticipants(
                id = ConversationParticipantsId(
                    conversationId = savedConversation.id,
                    userId = userId
                )
            )
        }
        conversationParticipantsRepository.saveAll(participants)

        return conversationRepository.getConversationById(conversionId = savedConversation.id, userId = requesterId)
            ?: throw IllegalStateException("Failed to retrieve the newly created conversation.")
    }

}