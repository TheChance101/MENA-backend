package net.thechance.chat.api.dto

import net.thechance.chat.entity.Chat
import net.thechance.chat.entity.Contact
import net.thechance.chat.entity.ContactUser
import net.thechance.chat.entity.Message
import org.springframework.data.domain.Page
import java.time.Instant
import java.util.*

data class ChatsResponse(
    val id: UUID,
    val name: String,
    val imageUrl: String?,
    val lastMessage: String?,
    val lastMessageTime: Instant?,
    val status: Status,
) {
    data class Status(
        val isMine: Boolean,
        val unReadMessagesCount: UInt
    )
}

fun Chat.toResponse(
    requesterId: UUID,
    contact: Contact?,
    message: Message? = null,
    unreadCount: Int = 0
): ChatsResponse {
    val theOtherUser = users.firstOrNull { it.id != requesterId }
    val status = ChatsResponse.Status(
        isMine = message?.senderId == requesterId,
        unReadMessagesCount = unreadCount.toUInt()
    )
    return ChatsResponse(
        id = id,
        name = getChatName(contact, theOtherUser),
        imageUrl = theOtherUser?.imageUrl,
        lastMessage = message?.text ?: "",
        lastMessageTime = message?.sentAt,
        status = status
    )
}


private fun getChatName(contact: Contact?, theOtherUser: ContactUser?): String {
    return contact?.let { "${it.firstName} ${it.lastName}" }
        ?: theOtherUser?.let { "${it.firstName} ${it.lastName}" }.orEmpty()
}
fun Page<ChatsResponse>.toPagedChatResponse(): PagedResponse<ChatsResponse> {
    return PagedResponse(
        data = this.content,
        pageNumber = this.number,
        pageSize = this.size,
        totalItems = this.totalElements,
        totalPages = this.totalPages
    )
}