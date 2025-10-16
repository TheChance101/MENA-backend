package net.thechance.chat.api.dto

import net.thechance.chat.entity.ChatSummary
import org.springframework.data.domain.Page
import java.time.Instant
import java.util.*

data class ChatSummaryResponse(
    val id: UUID,
    val name: String,
    val imageUrl: String?,
    val lastMessage: Message?,
    val unReadMessagesCount: Int
) {
    data class Message(
        val text: String,
        val sentAt: Instant,
        val isMine: Boolean
    )
}

fun ChatSummary.toResponse(): ChatSummaryResponse {
    return ChatSummaryResponse(
        id = id,
        name = name,
        imageUrl = imageUrl,
        lastMessage = lastMessage?.let {
            ChatSummaryResponse.Message(
                text = it.text,
                sentAt = it.sentAt,
                isMine = it.isMine
            )
        },
        unReadMessagesCount = unReadMessagesCount
    )
}


fun Page<ChatSummary>.toPagedResponse(): PagedResponse<ChatSummaryResponse> {
    return PagedResponse(
        data = this.content.map { it.toResponse() },
        pageNumber = this.number,
        pageSize = this.size,
        totalItems = this.totalElements,
        totalPages = this.totalPages
    )
}