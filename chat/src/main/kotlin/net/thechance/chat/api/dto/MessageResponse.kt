package net.thechance.chat.api.dto

import net.thechance.chat.entity.Message
import org.springframework.data.domain.Page
import java.time.Instant
import java.util.*

data class MessageRequestDto(
    val chatId: UUID,
    val text: String?,
    val messageId: UUID?
)

data class MessageResponse(
    val id: UUID,
    val senderId: UUID,
    val chatId: UUID,
    val text: String?,
    val images: List<String>,
    val sendAt: Instant,
    val isRead: Boolean,
    val isMine: Boolean
)


fun Message.toResponse(requesterId: UUID): MessageResponse {
    return MessageResponse(
        id = this.id,
        senderId = this.senderId,
        chatId = this.chat.id,
        text = this.text,
        images = this.images,
        sendAt = this.sentAt,
        isRead = this.isRead,
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