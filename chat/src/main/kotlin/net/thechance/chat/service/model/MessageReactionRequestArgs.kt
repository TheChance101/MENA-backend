package net.thechance.chat.service.model

import java.util.UUID

data class MessageReactionRequestArgs(
    val messageId: UUID,
    val userId: UUID,
    val emoji: String
)
