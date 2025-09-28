package net.thechance.chat.api.controller

import net.thechance.chat.api.dto.toResponse
import net.thechance.chat.service.ChatService
import net.thechance.chat.service.model.ChatModel
import net.thechance.chat.service.model.MessageModel
import org.springframework.http.ResponseEntity
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.messaging.handler.annotation.SendTo
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import java.util.*

@RequestMapping("/chat")
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



    @GetMapping
    @ResponseBody
    fun getOrCreateConversation(
        @AuthenticationPrincipal userId: UUID,
        @RequestParam receiverId: UUID
    ): ChatModel? {
        return chatService.getOrCreateConversationByParticipants(userId, receiverId)

    }

}