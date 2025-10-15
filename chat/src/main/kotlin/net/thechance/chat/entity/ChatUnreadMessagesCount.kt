package net.thechance.chat.entity

import java.util.UUID

interface ChatUnreadMessagesCount {
    val chatId: UUID
    val unreadCount: Int
}