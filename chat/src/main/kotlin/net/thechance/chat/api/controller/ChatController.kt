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
import java.time.Instant
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

        sendToChatUser(chatId = chatMessage.chatId) { message.toResponse(it) }
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
            chatService.getAllChatMessagesByChatId(chatId, pageable)
                .toPagedMessageResponse(userId)
        )
    }

    @GetMapping("/{chatId}/messages/latest")
    fun getLatestMessages(
        @PathVariable chatId: UUID,
        @RequestParam lastUpdateTime: String,
        @AuthenticationPrincipal userId: UUID,
        pageable: Pageable
    ): ResponseEntity<PagedResponse<MessageResponse>> {
        val since = Instant.parse(lastUpdateTime)
        val updatedMessages = chatService.getLatestMessagesAfter(chatId, since, pageable).toPagedMessageResponse(userId)
        return ResponseEntity.ok(updatedMessages)
    }

    @PostMapping("/image")
    fun sendMessageImage(
        @ModelAttribute request: MessageImageRequest,
        principal: Principal
    ): ResponseEntity<MessageResponse> {
        val senderId = UUID.fromString(principal.name)
        val messageImageArgs = request.toRequestArgs(senderId)
        val message = chatService.saveMessageImage(messageImageArgs)

        sendToChatUser(chatId = request.chatId) { message.toResponse(it) }

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

    @MessageMapping("/chat.addMessageReaction")
    fun addReaction(
        @Payload body: MessageReactionRequest,
        principal: Principal
    ) {
        val userId = UUID.fromString(principal.name)
        val message = chatService.getMessageById(body.messageId)
        val reactionResponse = chatService.addReaction(body.toRequestArgs(userId)).toResponse()

        sendToChatUser(message.chatId, ADD_REACTION) { reactionResponse }
    }

    @MessageMapping("/chat.deleteMessageReaction")
    fun deleteReaction(
        @Payload body: MessageReactionRequest,
        principal: Principal
    ) {
        val userId = UUID.fromString(principal.name)
        val message = chatService.getMessageById(body.messageId)
        val deletedReaction = chatService.deleteReaction(body.toRequestArgs(userId))

        sendToChatUser(message.chatId, DELETE_REACTION) { deletedReaction.toResponse() }
    }

    @MessageMapping("/chat.markAsRead")
    fun markMessagesAsRead(
        @Payload markAsReadRequest: MarkAsReadRequest,
        principal: Principal
    ) {
        val userId = UUID.fromString(principal.name)
        chatService.markChatMessagesAsRead(markAsReadRequest.chatId, userId)

        sendToChatUser(
            chatId = markAsReadRequest.chatId,
            destination = MARK_AS_READ
        ) { chatParticipantId ->
            MarkAsReadResponse(userId, markAsReadRequest.chatId, chatParticipantId == userId)
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

    private fun sendToChatUser(
        chatId: UUID,
        destination: String = PRIVATE_MESSAGES,
        payload: (userId: UUID) -> Any
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

    @DeleteMapping("/{chatId}")
    fun deleteChatById(
        @PathVariable chatId: UUID
    ): ResponseEntity<Unit> {
        chatService.deleteChatById(chatId)
        sendToChatUser(chatId, DELETE_CHAT){
            DeleteChatResponse(chatId)
        }
        return ResponseEntity.ok().body(Unit)
    }

    @GetMapping("/deletedChats")
    fun getDeletedChatsAfter(
        @RequestParam deletedAfter: String,
        @AuthenticationPrincipal userId: UUID
    ): ResponseEntity<List<String>> {
        val time = Instant.parse(deletedAfter)
        val deletedChats = chatService.getDeletedChatsIdByUserIdAfterSpecificTime(userId, time)
        return ResponseEntity.ok(deletedChats)
    }

    companion object {
        const val PRIVATE_MESSAGES = "/private/messages"
        const val MARK_AS_READ = "/private/markAsRead"
        const val ADD_REACTION = "/private/addReaction"
        const val DELETE_REACTION = "/private/deleteReaction"
        const val DELETE_CHAT = "/private/deleteChat"
    }
}