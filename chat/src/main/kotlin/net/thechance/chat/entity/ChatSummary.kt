package net.thechance.chat.entity

import java.time.Instant
import java.util.UUID

data class ChatSummary(
    val id: UUID,
    val name: String,
    val imageUrl: String?,
    val lastMessage: Message?,
    val unReadMessagesCount: Int
) {
    data class Message(
        val text: String,
        val sentAt: Instant,
        val isMine: Boolean
    )
}
