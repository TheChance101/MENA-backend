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
) {
    data class Message(
        val text: String,
        val sentAt: Instant,
        val isMine: Boolean
    )
}
fun Chat.toSummary(userId: UUID, chatName: String, imageUrl:String?, lastMessage: Message?, unreadCount: Int): ChatSummary {
    return ChatSummary(
        id = id,
        name = chatName,
        imageUrl = imageUrl,
        lastMessage = lastMessage?.let { msg ->
            val displayText = when(msg.content) {
                is MessageContent.Text -> msg.content.text
                is MessageContent.Image -> "Photo"
                is MessageContent.Audio -> "Audio"
                is MessageContent.Ayah -> "Ayah"
                is MessageContent.Money -> "Money"
                is MessageContent.Order -> "Order"
            }

            ChatSummary.Message(
                text = displayText,
                sentAt = msg.sentAt,
                isMine = msg.senderId == userId
            )
        },
        unReadMessagesCount = unreadCount
    )
}