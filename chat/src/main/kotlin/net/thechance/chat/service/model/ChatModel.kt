package net.thechance.chat.service.model

import net.thechance.chat.entity.Chat
import java.util.UUID

data class ChatModel(
    val name: String,
    val imageUrl: String?,
    val requesterId: UUID,
    val receiverId: UUID,
    val id: UUID,
)

fun Chat.toModel(chatName: String, imageUrl:String, requesterId: UUID): ChatModel{
    return ChatModel(
        name = chatName,
        imageUrl = imageUrl,
        requesterId = requesterId,
        receiverId = this.users.first { it.id != requesterId }.id,
        id = this.id
    )
}
