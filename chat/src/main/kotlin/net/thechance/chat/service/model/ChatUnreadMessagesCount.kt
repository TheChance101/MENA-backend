package net.thechance.chat.service.model

import java.util.UUID

interface ChatUnreadMessagesCount {
    val chatId: UUID
    val unreadCount: Int
}