package net.thechance.chat.service.model

import java.util.UUID

class ConversationModel(
    val id: UUID,
    val isGroup: Boolean,
    val name: String?,
    val imageUrl: String?,
)