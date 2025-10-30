package net.thechance.chat.api.dto

import net.thechance.chat.api.dto.MessageResponse.Reaction
import net.thechance.chat.entity.Message
import net.thechance.chat.service.model.MessageWithReactions
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
    val reactions: List<Reaction> = emptyList(),
    val sendAt: Instant,
    val isRead: Boolean,
    val isMine: Boolean
) {
    data class Reaction(
        val emoji: String,
        val userId: UUID
    )
}

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

fun MessageWithReactions.toResponse(requesterId: UUID): MessageResponse {

    return MessageResponse(
        id = message.id,
        senderId = message.senderId,
        chatId = message.chatId,
        text = message.text,
        imageUrl = message.imageUrl,
        reactions = reactions.map { Reaction(emoji = it.emoji, userId = it.userId) },
        sendAt = message.sentAt,
        isRead = message.isRead,
        isMine = requesterId == message.senderId
    )
}

fun Page<MessageWithReactions>.toPagedMessageResponse(requesterId: UUID): PagedResponse<MessageResponse> {
    return PagedResponse(
        data = this.content.map { it.toResponse(requesterId) },
        pageNumber = this.number,
        pageSize = this.size,
        totalItems = this.totalElements,
        totalPages = this.totalPages
    )
}