package net.thechance.chat.api.dto

import net.thechance.chat.entity.Chat
import net.thechance.chat.entity.Contact
import net.thechance.chat.entity.ContactUser
import java.util.UUID

data class ChatResponse(
    val id: UUID,
    val name: String,
    val requesterId: UUID,
    val imageUrl: String?,
)

fun Chat.toResponse(requesterId: UUID, contact: Contact?): ChatResponse {
    val theOtherUser = users.firstOrNull { it.id != requesterId }
    return ChatResponse(
        id = id,
        name = getChatName(contact, theOtherUser),
        requesterId = requesterId,
        imageUrl = theOtherUser?.imageUrl,
    )
}

private fun getChatName(contact: Contact?, theOtherUser: ContactUser?): String {
    return contact?.let { "${it.firstName} ${it.lastName}" }
        ?: theOtherUser?.let { "${it.firstName} ${it.lastName}" }.orEmpty()
}


