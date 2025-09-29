package net.thechance.chat.api.dto

import net.thechance.chat.entity.Message
import java.time.Instant
import java.util.UUID

class MessageResponse(
    val id: UUID,
    val senderId: UUID,
    val chatId: UUID,
    val text: String,
    val sentAt: Instant,
    val isRead: Boolean
)

fun Message.toResponse() = MessageResponse(
    id = id,
    senderId = senderId,
    chatId = chat.id,
    text = text,
    sentAt = sentAt,
    isRead = isRead
)