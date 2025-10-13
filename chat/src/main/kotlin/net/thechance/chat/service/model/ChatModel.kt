package net.thechance.chat.service.model

import net.thechance.chat.api.dto.ChatResponse
import java.util.UUID

data class ChatModel (
    val name : String,
    val imageUrl : String?,
    val requesterId : UUID,
    val id: UUID,
    )

fun ChatModel.toResponse(): ChatResponse{
    return ChatResponse(
        id = id,
        name = name,
        requesterId = requesterId,
        imageUrl = imageUrl
    )
}