package net.thechance.chat.api.dto

import java.util.UUID

data class MarkAsReadResponse(
    val readByUserId: UUID,
    val chatId: UUID,
    val readByMe: Boolean
)