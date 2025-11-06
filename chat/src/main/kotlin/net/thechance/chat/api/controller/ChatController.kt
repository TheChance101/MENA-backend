package net.thechance.chat.api.controller

import net.thechance.chat.api.dto.*
import net.thechance.chat.service.ChatService
import net.thechance.chat.service.model.toRequestArgs
import org.springframework.data.domain.Pageable
import org.springframework.http.ResponseEntity
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import java.security.Principal
import java.util.UUID

@RequestMapping("/chat")
@Controller
class ChatController(
    private val messagingTemplate: SimpMessagingTemplate,
    private val chatService: ChatService,
) {

    @MessageMapping("/chat.privateMessage")
    fun sendPrivateMessage(
        @Payload chatMessage: MessageRequestDto,
        principal: Principal
    ) {
        val senderId = UUID.fromString(principal.name)
        val message = chatService.saveMessage(chatMessage.toRequestArgs(senderId))

        chatService
            .getChatUsersIds(chatId = chatMessage.chatId)
            .forEach { chatParticipantId ->
                messagingTemplate.convertAndSendToUser(
                    chatParticipantId.toString(),
                    PRIVATE_MESSAGES,
                    message.toResponse(chatParticipantId)
                )
            }
    }

    @GetMapping
    @ResponseBody
    fun getChatByUserIds(
        @AuthenticationPrincipal userId: UUID,
        @RequestParam receiverId: UUID
    ): ResponseEntity<ChatResponse> {
        val chat = chatService.getChatByUserIds(userId, receiverId).toResponse()
        return ResponseEntity.ok(chat)
    }

    @GetMapping("/{chatId}/messages")
    fun getChatHistory(
        @PathVariable chatId: UUID,
        @AuthenticationPrincipal userId: UUID,
        pageable: Pageable
    ): ResponseEntity<PagedResponse<MessageResponse>> {
        return ResponseEntity.ok(
            chatService.getAllChatMessages(chatId, pageable).toPagedMessageResponse(userId)
        )
    }

    @PostMapping("/image")
    fun sendMessageImage(
        @ModelAttribute request: MessageImageRequest,
        principal: Principal
    ): ResponseEntity<MessageResponse> {
        val senderId = UUID.fromString(principal.name)
        val messageImageArgs = request.toRequestArgs(senderId)
        val message = chatService.saveMessageImage(messageImageArgs)
        chatService
            .getChatUsersIds(chatId = request.chatId)
            .forEach { chatParticipantId ->
                messagingTemplate.convertAndSendToUser(
                    chatParticipantId.toString(),
                    PRIVATE_MESSAGES,
                    message.toResponse(chatParticipantId)
                )
            }

        return ResponseEntity.ok(message.toResponse(senderId))
    }

    @PostMapping("/audio")
    fun sendMessageAudio(
        @ModelAttribute request: MessageAudioRequest,
        @AuthenticationPrincipal senderId: UUID,
    ): ResponseEntity<MessageResponse> {
        val messageAudioArgs = request.toRequestArgs(senderId)
        val message = chatService.saveMessageAudio(messageAudioArgs)
        chatService.getChatUsersIds(chatId = request.chatId).forEach { chatParticipantId ->
            messagingTemplate.convertAndSendToUser(
                chatParticipantId.toString(),
                PRIVATE_MESSAGES,
                message.toResponse(chatParticipantId)
            )
        }
        return ResponseEntity.ok(message.toResponse(senderId))
    }


    @MessageMapping("/chat.markAsRead")
    fun markMessagesAsRead(
        @Payload markAsReadRequest: MarkAsReadRequest,
        principal: Principal
    ) {
        val userId = UUID.fromString(principal.name)
        chatService.markChatMessagesAsRead(markAsReadRequest.chatId, userId)

        chatService
            .getChatUsersIds(chatId = markAsReadRequest.chatId)
            .forEach { chatParticipantId ->
                messagingTemplate.convertAndSendToUser(
                    chatParticipantId.toString(),
                    PRIVATE_MESSAGES,
                    MarkAsReadResponse(userId, markAsReadRequest.chatId, chatParticipantId == userId)
                )
            }
    }

    @GetMapping("/chatsSummary")
    fun getUserChatSummary(
        @AuthenticationPrincipal userId: UUID,
        pageable: Pageable
    ): ResponseEntity<PagedResponse<ChatSummaryResponse>> {
        val chats = chatService.getUserChatsSummaries(userId, pageable)
        return ResponseEntity.ok(chats.toPagedResponse())
    }

    @GetMapping("/{chatId}/summary")
    fun getUserChatSummaryById(
        @AuthenticationPrincipal userId: UUID,
        @PathVariable chatId: UUID,
    ): ResponseEntity<ChatSummaryResponse> {
        val chat = chatService.getUserChatSummaryById(chatId, userId)
        return ResponseEntity.ok(chat.toResponse())
    }

    @GetMapping("/{chatId}")
    fun getChatDetail(
        @PathVariable chatId: UUID,
        @AuthenticationPrincipal userId: UUID
    ): ResponseEntity<ChatResponse> {
        val chat = chatService.getChatById(chatId, userId)
        return ResponseEntity.ok(chat.toResponse())
    }

    @DeleteMapping("/{chatId}")
    fun deleteChatById(
        @PathVariable chatId: UUID
    ): ResponseEntity<Unit> {
        chatService.deleteChatById(chatId)
        sendMessageToUsers(chatId, DeleteChatResponse(chatId))
        return ResponseEntity.ok().body(Unit)
    }

    private fun sendMessageToUsers(chatId: UUID, payload: Any) {
        chatService.getChatUsersIds(chatId).forEach { chatParticipantId ->
            messagingTemplate.convertAndSendToUser(
                chatParticipantId.toString(),
                PRIVATE_MESSAGES,
                payload
            )
        }
    }
    companion object {
        const val PRIVATE_MESSAGES = "/private/messages"
    }
}