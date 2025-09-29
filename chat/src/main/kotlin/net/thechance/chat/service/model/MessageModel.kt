package net.thechance.chat.service.model

import net.thechance.chat.entity.Message
import java.time.Instant
import java.util.UUID

class MessageModel(
    val id: UUID,
    val senderId: UUID,
    val chatId: UUID,
    val text: String,
    val sendAt: Instant,
    val isRead: Boolean
)

fun Message.toModel() = MessageModel(
    id = id,
    senderId = senderId,
    chatId = chat.id,
    text = text,
    sendAt = sendAt,
    isRead = isRead
)