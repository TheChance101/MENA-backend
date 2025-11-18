package net.thechance.chat.api.dto

import net.thechance.chat.service.model.ChatSummary
import org.springframework.data.domain.Page
import java.time.Instant
import java.util.UUID

data class ChatSummaryResponse(
    val id: UUID,
    val name: String,
    val imageUrl: String?,
    val lastMessage: MessageResponse?,
    val unReadMessagesCount: Int
)

fun ChatSummary.toResponse(requesterId: UUID): ChatSummaryResponse {
    return ChatSummaryResponse(
        id = id,
        name = name,
        imageUrl = imageUrl,
        lastMessage = lastMessage?.toResponse(requesterId),
        unReadMessagesCount = unReadMessagesCount
    )
}


fun Page<ChatSummary>.toPagedResponse(requesterId: UUID): PagedResponse<ChatSummaryResponse> {
    return PagedResponse(
        data = this.content.map { it.toResponse(requesterId) },
        pageNumber = this.number,
        pageSize = this.size,
        totalItems = this.totalElements,
        totalPages = this.totalPages
    )
}