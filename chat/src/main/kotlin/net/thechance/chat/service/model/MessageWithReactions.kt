package net.thechance.chat.service.model

import net.thechance.chat.entity.Message
import net.thechance.chat.entity.MessageReaction

data class MessageWithReactions(
    val message: Message,
    val reactions: List<MessageReaction>
)
