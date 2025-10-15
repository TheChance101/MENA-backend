package net.thechance.chat.service

import net.thechance.chat.entity.Chat
import net.thechance.chat.entity.ChatSummary
import net.thechance.chat.entity.ContactUser
import net.thechance.chat.entity.Message
import java.util.UUID

fun Chat.toSummary(userId: UUID, otherUser: ContactUser?, lastMessage: Message?, unreadCount: Int): ChatSummary {
    return ChatSummary(
        id = id,
        name = otherUser?.let { "${otherUser.firstName} ${otherUser.lastName}" } ?: "",
        imageUrl = otherUser?.imageUrl,
        lastMessage = lastMessage?.let {
            ChatSummary.Message(
                text = lastMessage.text,
                sentAt = lastMessage.sentAt,
                isMine = lastMessage.senderId == userId
            )
        },
        unReadMessagesCount = unreadCount
    )
}