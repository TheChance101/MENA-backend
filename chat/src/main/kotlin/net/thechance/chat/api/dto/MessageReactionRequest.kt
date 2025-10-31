package net.thechance.chat.api.dto

import net.thechance.chat.service.model.MessageReactionRequestArgs
import java.util.UUID

data class MessageReactionRequest(
    val messageId: UUID,
    val emoji: String
)

fun MessageReactionRequest.toRequestArgs(userId: UUID): MessageReactionRequestArgs {
    return MessageReactionRequestArgs(messageId, userId, emoji)
}