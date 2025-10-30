package net.thechance.chat.api.dto

import net.thechance.chat.service.model.MessageReactionRequestArgs
import java.util.UUID

data class MessageReactionRequest(
    val emoji: String
)

fun MessageReactionRequest.toRequestArgs(messageId: UUID, userId: UUID): MessageReactionRequestArgs {
    return MessageReactionRequestArgs(messageId, userId, emoji)
}