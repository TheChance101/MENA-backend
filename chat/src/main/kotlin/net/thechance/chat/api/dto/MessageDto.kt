package net.thechance.chat.api.dto

import net.thechance.chat.entity.Message
import net.thechance.chat.service.args.CreateMessageArgs
import org.springframework.data.domain.Page
import java.time.Instant
import java.util.*

data class MessageRequestDto(
    val chatId: UUID,
    val text: String,
)

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
        sendAt = this.sentAt,
        isRead = this.isRead
    )
}

fun MessageRequestDto.toCreateMessageArgs(senderId: UUID): CreateMessageArgs {
    return CreateMessageArgs(
        id = UUID.randomUUID(),
        senderId = senderId,
        chatId = this.chatId,
        text = this.text,
        sendAt = Instant.now(),
        isRead = false
    )
}

fun Page<Message>.toPagedMessageResponse(): PagedResponse<MessageDto> {
    return PagedResponse(
        data = this.content.map { it.toDto() },
        pageNumber = this.number,
        pageSize = this.size,
        totalItems = this.totalElements,
        totalPages = this.totalPages
    )
}