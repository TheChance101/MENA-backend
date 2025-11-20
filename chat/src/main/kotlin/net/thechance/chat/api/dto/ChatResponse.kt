package net.thechance.chat.api.dto

import net.thechance.chat.service.model.ChatModel
import java.util.*

data class ChatResponse(
    val id: UUID,
    val name: String,
    val requesterId: UUID,
    val imageUrl: String?,
    val receiverId: UUID,
)

fun ChatModel.toResponse(): ChatResponse{
    return ChatResponse(
        id = id,
        name = name,
        requesterId = requesterId,
        imageUrl = imageUrl,
        receiverId = receiverId,
    )
}

