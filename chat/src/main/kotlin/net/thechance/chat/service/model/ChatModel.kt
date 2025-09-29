package net.thechance.chat.service.model

import net.thechance.chat.entity.Chat
import java.util.UUID

data class ChatModel(
    val id: UUID,
    val name: String,
    val imageUrl: String?,
)

fun Chat.toModel(requesterId: UUID) = ChatModel(
    id = id,
    name = users.firstOrNull { it.id != requesterId }
        ?.let { "${it.firstName} ${it.lastName}" }
        .orEmpty(),
    imageUrl = users.firstOrNull { it.id != requesterId }?.imageUrl,
)