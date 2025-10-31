package net.thechance.chat.service.model

import net.thechance.chat.entity.Chat
import java.util.UUID

data class ChatModel(
    val name: String,
    val imageUrl: String?,
    val requesterId: UUID,
    val id: UUID,
)

fun Chat.toModel(chatName: String, imageUrl:String ,requesterId: UUID): ChatModel{
    return ChatModel(
        name = chatName,
        imageUrl = imageUrl,
        requesterId = requesterId,
        id = this.id
    )
}
