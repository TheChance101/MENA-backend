package net.thechance.chat.service.model

import net.thechance.chat.entity.Chat
import net.thechance.chat.entity.Message
import java.time.Instant
import java.util.UUID

data class ChatSummary(
    val id: UUID,
    val name: String,
    val imageUrl: String?,
    val lastMessage: Message?,
    val unReadMessagesCount: Int
)

fun Chat.toSummary(chatName: String, imageUrl: String?, lastMessage: Message?, unreadCount: Int): ChatSummary {
    return ChatSummary(
        id = id,
        name = chatName,
        imageUrl = imageUrl,
        lastMessage = lastMessage,
        unReadMessagesCount = unreadCount
    )
}