package net.thechance.chat.api.controller

import net.thechance.chat.api.dto.*
import net.thechance.chat.service.ChatService
import org.springframework.data.domain.Pageable
import org.springframework.http.ResponseEntity
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import java.time.Instant
import java.util.*

@RequestMapping("/chat")
@Controller
class ChatController(
    private val messagingTemplate: SimpMessagingTemplate,
    private val chatService: ChatService,
) {

    @MessageMapping("/chat.privateMessage")
    fun sendPrivateMessage(@Payload chatMessage: MessageDto) {
        val updatedMessage = chatMessage.copy(sendAt = Instant.now())
        chatService.saveMessage(updatedMessage)
        messagingTemplate.convertAndSendToUser(
            chatMessage.chatId.toString(),
            "/queue/messages",
            updatedMessage
        )
    }

    @GetMapping
    @ResponseBody
    fun getOrCreateConversation(
        @AuthenticationPrincipal userId: UUID,
        @RequestParam receiverId: UUID
    ): ResponseEntity<ChatResponse> {
        return ResponseEntity.ok(chatService.getOrCreateConversationByParticipants(userId, receiverId).toResponse(userId))
    }

    @GetMapping("/history")
    fun getChatHistory(
        @RequestParam chatId: UUID,
        pageable: Pageable
    ): ResponseEntity<PagedResponse<MessageDto>> {
        return ResponseEntity.ok(
            chatService.getAllChatMessages(
                chatId = chatId,
                pageable = pageable
            ).toPagedMessageResponse()
        )
    }

    @PatchMapping
    fun markMessagesAsRead(
        @AuthenticationPrincipal userId: UUID,
        @RequestParam chatId: UUID,
    ) {
        messagingTemplate.convertAndSendToUser(
            chatId.toString(),
            "/queue/messages",
            mapOf("readBy" to userId)
        )
        chatService.markChatMessagesAsRead(chatId, userId)
    }
}