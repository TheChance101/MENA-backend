package net.thechance.chat.service

import net.thechance.chat.entity.Message
import net.thechance.chat.repository.MessageRepository
import net.thechance.chat.service.model.MessageModel
import org.springframework.stereotype.Service

@Service
class ChatService(private val messageRepository: MessageRepository) {

    fun saveMessage(chatMessage: MessageModel) : Message {
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


}