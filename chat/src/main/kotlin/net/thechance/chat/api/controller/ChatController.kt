package net.thechance.chat.api.controller

import net.thechance.chat.service.ChatService
import net.thechance.chat.service.model.MessageModel
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.messaging.handler.annotation.SendTo
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Controller

@Controller
class ChatController(
    private val messagingTemplate: SimpMessagingTemplate,
    private val chatService: ChatService,
) {

    @MessageMapping("/chat.privateMessage")
    fun sendPrivateMessage(@Payload chatMessage: MessageModel) {
        chatService.saveMessage(chatMessage)
        messagingTemplate.convertAndSendToUser(
            chatMessage.chatId.toString(),   //  recipientId
            "/queue/messages",
            chatMessage
        )
    }

    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic/public")
    fun sendMessage(@Payload chatMessage: MessageModel): MessageModel {
        chatService.saveMessage(chatMessage)
        return chatMessage
    }

    @MessageMapping("/chat.addUser")
    @SendTo("/topic/public")
    fun addUser(@Payload chatMessage: MessageModel): MessageModel {
        chatService.saveMessage(chatMessage)
        return chatMessage
    }

}