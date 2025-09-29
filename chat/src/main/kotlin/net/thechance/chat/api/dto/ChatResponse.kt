package net.thechance.chat.api.dto

import net.thechance.chat.entity.Chat
import java.util.UUID

class ChatResponse(
    val id: UUID,
    val name: String,
    val imageUrl: String?,
)

fun Chat.toResponse(requesterId: UUID) = ChatResponse(
    id = id,
    name = users.firstOrNull { it.id != requesterId }
        ?.let { "${it.firstName} ${it.lastName}" }
        .orEmpty(),
    imageUrl = users.firstOrNull { it.id != requesterId }?.imageUrl,
)