package net.thechance.chat.service

import net.thechance.chat.entity.Chat
import net.thechance.chat.entity.ChatSummary
import net.thechance.chat.entity.ContactUser
import net.thechance.chat.entity.Message
import java.util.*

fun Chat.toSummary(
    userId: UUID,
    otherUser: ContactUser?,
    lastMessage: Message?,
    unreadCount: Int
): ChatSummary {
    return ChatSummary(
        id = id,
        name = otherUser?.let { "${otherUser.firstName} ${otherUser.lastName}" } ?: "",
        imageUrl = otherUser?.imageUrl,
        lastMessage = lastMessage?.let { msg ->
            val displayText = when {
                !msg.text.isNullOrEmpty() -> msg.text
                !msg.imageUrl.isNullOrEmpty() -> "Photo"
                !msg.audioUrl.isNullOrEmpty() -> "Audio"
                else -> "Unsupported"
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