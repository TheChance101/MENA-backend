package net.thechance.chat.service.args

import java.time.Instant
import java.util.*

data class CreateMessageArgs(
    val id: UUID,
    val senderId: UUID,
    val chatId: UUID,
    val text: String,
    val sendAt: Instant,
    val isRead: Boolean
)