package net.thechance.chat.service.model

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