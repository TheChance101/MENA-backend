package net.thechance.chat.api.dto

import net.thechance.chat.entity.Message
import net.thechance.chat.entity.MessageReaction
import net.thechance.chat.service.model.MessageContent
import org.springframework.data.domain.Page
import java.time.Instant
import java.util.*

data class MessageRequestDto(
    val chatId: UUID,
    val text: String
)

data class MessageResponse(
    val id: UUID,
    val senderId: UUID,
    val chatId: UUID,
    val type: String,
    val content: MessageContentResponse,
    val reactions: List<MessageReactionResponse> = emptyList(),
    val sendAt: Instant,
    val updatedAt: Instant,
    val isRead: Boolean,
    val isMine: Boolean
)

fun MessageContent.toResponse() = when(this) {
    is MessageContent.Text -> MessageContentResponse.Text(text)
    is MessageContent.Image -> MessageContentResponse.Image(url)
    is MessageContent.Audio -> MessageContentResponse.Audio(url, durationMs)
}

sealed class MessageContentResponse {
    data class Text(val text: String) : MessageContentResponse()
    data class Image(val url: String) : MessageContentResponse()
    data class Audio(val url: String, val duration: Long) : MessageContentResponse()
}


fun Message.toResponse(requesterId: UUID): MessageResponse {
    return MessageResponse(
        id = id,
        senderId = senderId,
        chatId = chatId,
        type = type.name,
        content = content.toResponse(),
        reactions = reactions.map(MessageReaction::toResponse),
        sendAt = sentAt,
        updatedAt = lastModifiedAt,
        isRead = isRead,
        isMine = requesterId == senderId
    )
}

fun Page<Message>.toPagedMessageResponse(requesterId: UUID): PagedResponse<MessageResponse> {
    return PagedResponse(
        data = this.content.map { it.toResponse(requesterId) },
        pageNumber = this.number,
        pageSize = this.size,
        totalItems = this.totalElements,
        totalPages = this.totalPages
    )
}