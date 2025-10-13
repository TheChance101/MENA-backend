package net.thechance.chat.api.dto

import net.thechance.chat.entity.Message
import org.springframework.data.domain.Page
import java.time.Instant
import java.util.*

data class MessageRequestDto(
    val chatId: UUID,
    val messageId: UUID?,
    val text: String?
)

data class MessageResponseDto(
    val id: UUID,
    val senderId: UUID,
    val chatId: UUID,
    val text: String?,
    val images: List<String>,
    val sendAt: Instant,
    val isRead: Boolean
)


fun Message.toDto(): MessageResponseDto {
    return MessageResponseDto(
        id = this.id,
        senderId = this.senderId,
        chatId = this.chat.id,
        text = this.text,
        images = this.images.map { it.url },
        sendAt = this.sentAt,
        isRead = this.isRead
    )
}

fun Page<Message>.toPagedMessageResponse(): PagedResponse<MessageResponseDto> {
    return PagedResponse(
        data = this.content.map { it.toDto() },
        pageNumber = this.number,
        pageSize = this.size,
        totalItems = this.totalElements,
        totalPages = this.totalPages
    )
}


data class MessageRequestArgs(
    val chatId: UUID,
    val senderId: UUID,
    val text: String?,
    val messageId: UUID?
)

fun MessageRequestDto.toReqArgs(senderId: UUID): MessageRequestArgs {
    return MessageRequestArgs(
        chatId = this.chatId,
        senderId = senderId,
        text = this.text,
        messageId = this.messageId
    )
}