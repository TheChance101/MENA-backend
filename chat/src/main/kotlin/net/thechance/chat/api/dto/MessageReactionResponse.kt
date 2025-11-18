package net.thechance.chat.api.dto

import net.thechance.chat.entity.MessageReaction
import java.util.UUID

data class MessageReactionResponse(
    val messageId: UUID,
    val userId: UUID,
    val emoji: String
)

fun MessageReaction.toResponse(): MessageReactionResponse {
    return MessageReactionResponse(messageId, userId, emoji)
}