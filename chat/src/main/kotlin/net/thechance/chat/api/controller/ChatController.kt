package net.thechance.chat.api.controller

import net.thechance.chat.api.dto.*
import net.thechance.chat.service.ChatService
import net.thechance.chat.service.ContactService
import org.springframework.data.domain.Pageable
import org.springframework.http.ResponseEntity
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import java.security.Principal
import java.util.*

@RequestMapping("/chat")
@Controller
class ChatController(
    private val messagingTemplate: SimpMessagingTemplate,
    private val chatService: ChatService,
    private val contactService: ContactService,
) {

    @MessageMapping("/chat.privateMessage")
    fun sendPrivateMessage(@Payload chatMessage: MessageRequestDto, principal: Principal) {
        val senderId = UUID.fromString(principal.name)
        val createdMessage = chatMessage.toCreateMessageArgs(senderId = senderId)
        chatService.saveMessage(createdMessage)
        sendMessageToUser(
            user = chatMessage.chatId.toString(),
            message = createdMessage
        )
    }

    @GetMapping
    @ResponseBody
    fun getOrCreateConversation(
        @AuthenticationPrincipal userId: UUID,
        @RequestParam receiverId: UUID
    ): ResponseEntity<ChatResponse> {
        val contact = contactService.getContactByOwnerIdAndContactUserId(userId, receiverId)
        val chat = chatService.getOrCreateConversationByParticipants(userId, receiverId)
            .toResponse(userId, contact)
        return ResponseEntity.ok(chat)
    }

    @GetMapping("/history")
    fun getChatHistory(
        @RequestParam chatId: UUID,
        pageable: Pageable
    ): ResponseEntity<PagedResponse<MessageDto>> {
        return ResponseEntity.ok(
            chatService.getAllChatMessages(chatId,pageable).toPagedMessageResponse()
        )
    }

    @MessageMapping("/chat.markAsRead")
    fun markMessagesAsRead(
        @Payload markAsReadRequest: MarkAsReadRequest,
        principal: Principal
    ) {
        val userId = UUID.fromString(principal.name)
        sendMessageToUser(
            user = markAsReadRequest.chatId.toString(),
            message = MarkAsReadResponse(userId)
        )
        chatService.markChatMessagesAsRead(markAsReadRequest.chatId, userId)
    }

    private fun sendMessageToUser(user: String, message: Any) {
        messagingTemplate.convertAndSendToUser(
            user,
            QUEUE_MESSAGES,
            message
        )
    }

    companion object {
        const val QUEUE_MESSAGES = "/queue/messages"
    }
}