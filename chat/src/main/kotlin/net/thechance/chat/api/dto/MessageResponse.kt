package net.thechance.chat.api.dto

import net.thechance.chat.entity.Message
import net.thechance.chat.entity.MessageReaction
import net.thechance.chat.service.model.MessageContent
import org.springframework.data.domain.Page
import java.time.Instant
import java.util.*

data class TextMessageRequestDto(
    val messageId: UUID,
    val chatId: UUID,
    val text: String
)

data class MessageResponse(
    val id: UUID,
    val senderId: UUID,
    val chatId: UUID,
    val content: MessageResponse.MessageContent,
    val reactions: List<MessageReactionResponse> = emptyList(),
    val sendAt: Instant,
    val updatedAt: Instant,
    val isRead: Boolean,
    val isMine: Boolean
) {
    sealed class MessageContent {
        abstract val type: String

        data class Text(val text: String) : MessageContent() {
            override val type = Message.MessageType.TEXT.name
        }

        data class Image(val url: String) : MessageContent() {
            override val type = Message.MessageType.IMAGE.name
        }

        data class Audio(val url: String, val duration: Long) : MessageContent() {
            override val type = Message.MessageType.AUDIO.name
        }
    }

}

fun MessageContent.toResponse() = when(this) {
    is MessageContent.Text -> MessageResponse.MessageContent.Text(text)
    is MessageContent.Image -> MessageResponse.MessageContent.Image(url)
    is MessageContent.Audio -> MessageResponse.MessageContent.Audio(url, durationMs)
}



fun Message.toResponse(requesterId: UUID): MessageResponse {
    return MessageResponse(
        id = id,
        senderId = senderId,
        chatId = chatId,
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