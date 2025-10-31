package net.thechance.chat.service.model

import net.thechance.chat.entity.Chat
import net.thechance.chat.entity.ContactUser
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
fun Chat.toSummary(userId: UUID, otherUser: ContactUser?, lastMessage: Message?, unreadCount: Int): ChatSummary {
    return ChatSummary(
        id = id,
        name = otherUser?.let { "${otherUser.firstName} ${otherUser.lastName}" } ?: "",
        imageUrl = otherUser?.imageUrl,
        lastMessage = lastMessage?.let {
            ChatSummary.Message(
                text = lastMessage.text ?: "photo",
                sentAt = lastMessage.sentAt,
                isMine = lastMessage.senderId == userId
            )
        },
        unReadMessagesCount = unreadCount
    )
}


