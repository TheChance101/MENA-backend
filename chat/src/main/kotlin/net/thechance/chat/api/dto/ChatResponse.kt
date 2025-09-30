package net.thechance.chat.api.dto

import net.thechance.chat.entity.Chat
import net.thechance.chat.entity.Contact
import java.util.UUID

data class ChatResponse(
    val id: UUID,
    val name: String,
    val requesterId: UUID,
    val imageUrl: String?,
)

fun Chat.toResponse(requesterId: UUID, contact: Contact?) = ChatResponse(
    id = id,
    name = contact?.let { "${it.firstName} ${it.lastName}" }
        ?: users.firstOrNull { it.id != requesterId }
            ?.let { "${it.firstName} ${it.lastName}" }
            .orEmpty(),
    requesterId = requesterId,
    imageUrl = users.firstOrNull { it.id != requesterId }?.imageUrl,
)