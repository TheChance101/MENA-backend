package net.thechance.chat.service.model

import net.thechance.chat.entity.Chat
import java.util.UUID

class ChatModel(
    val id: UUID,
    val isGroup: Boolean,
    val name: String,
    val imageUrl: String?,
)

fun Chat.toModel(requesterId: UUID) = ChatModel(
    id = id,
    isGroup = groupChat != null,
    name = groupChat?.groupName ?: users.firstOrNull { it.id != requesterId }
        ?.let { "${it.firstName} ${it.lastName}" }
        .orEmpty(),
    imageUrl = groupChat?.groupImageUrl ?: users.firstOrNull { it.id != requesterId }?.imageUrl,
)