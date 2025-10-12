package net.thechance.chat.api.dto

import java.util.UUID

data class MarkAsReadRequest(
    val chatId: UUID
)

data class MarkAsReadResponse(
    val readBy: UUID
)