package net.thechance.chat.api.dto

import net.thechance.chat.entity.Message
import org.springframework.data.domain.Page
import java.time.Instant
import java.util.*

data class MessageRequestDto(
    val chatId: UUID,
    val text: String?
)

data class MessageResponse(
    val id: UUID,
    val senderId: UUID,
    val chatId: UUID,
    val text: String?,
    val imageUrl: String?,
    val sendAt: Instant,
    val isRead: Boolean,
    val isMine: Boolean
)

fun Message.toResponse(requesterId: UUID): MessageResponse {
    return MessageResponse(
        id = id,
        senderId = senderId,
        chatId = chatId,
        text = text,
        imageUrl = imageUrl,
        sendAt = sentAt,
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