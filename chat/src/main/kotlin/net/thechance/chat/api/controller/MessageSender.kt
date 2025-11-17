package net.thechance.chat.api.controller

import net.thechance.chat.service.ChatService
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class MessageSender(
    private val messagingTemplate: SimpMessagingTemplate,
    private val chatService: ChatService,
) {
    fun sendMessageToUser(
        receiverId: UUID,
        destination: String,
        message: Any
    ) {
        messagingTemplate.convertAndSendToUser(
            receiverId.toString(),
            destination,
            message
        )
    }

    fun sendMessageToChat(
        chatId: UUID,
        destination: String,
        payload: (receiverId: UUID) -> Any,
    ) {
        chatService
            .getChatUsersIds(chatId = chatId)
            .forEach { chatParticipantId ->
                messagingTemplate.convertAndSendToUser(
                    chatParticipantId.toString(),
                    destination,
                    payload(chatParticipantId)
                )
            }
    }
}