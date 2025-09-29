package net.thechance.chat.api.dto

import net.thechance.chat.entity.Message
import org.springframework.data.domain.Page
import java.time.Instant
import java.util.*

data class MessageDto(
    val id: UUID,
    val senderId: UUID,
    val chatId: UUID,
    val text: String,
    val sendAt: Instant,
    val isRead: Boolean
)


fun Message.toDto(): MessageDto {
    return MessageDto(
        id = this.id,
        senderId = this.senderId,
        chatId = this.chat.id,
        text = this.text,
        sendAt = this.sendAt,
        isRead = this.isRead
    )
}


fun Page<Message>.toPagedResponse(): PagedResponse<MessageDto> {
    return PagedResponse(
        data = this.content.map { it.toDto() },
        pageNumber = this.number + 1,
        pageSize = this.size,
        totalItems = this.totalElements,
        totalPages = this.totalPages
    )
}