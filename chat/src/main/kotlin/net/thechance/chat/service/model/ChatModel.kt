package net.thechance.chat.service.model

import java.util.UUID

data class ChatModel (
    val name : String,
    val imageUrl : String?,
    val requesterId : UUID,
    val id: UUID,
    )
