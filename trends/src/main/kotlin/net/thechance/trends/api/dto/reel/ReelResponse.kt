package net.thechance.trends.api.dto.reel

import net.thechance.trends.entity.Category
import net.thechance.trends.entity.Reel
import org.springframework.http.ResponseEntity
import java.time.LocalDateTime
import java.util.*

data class ReelResponse(
    val reelId: UUID,
    val thumbnailUrl: String?,
    val videoUrl: String,
    val description: String,
    val createdAt: LocalDateTime,
    val likesCount: Int,
    val viewsCount: Int,
    val isCurrentUserOwner: Boolean,
    val categories: Set<Category> = emptySet()
)

fun Reel.toResponse(): ReelResponse {
    return ReelResponse(
        reelId = id,
        thumbnailUrl = thumbnailUrl,
        videoUrl = videoUrl,
        description = description,
        createdAt = createdAt,
        likesCount = likesCount,
        viewsCount = viewsCount,
        isCurrentUserOwner = false,
        categories = categories,
    )
}

fun ReelResponse.withOwnership(currentUserId: UUID, ownerId: UUID): ReelResponse {
    return this.copy(isCurrentUserOwner = currentUserId == ownerId)
}

fun Reel.getResponseEntity(currentUserId: UUID): ResponseEntity<ReelResponse> {
    return ResponseEntity.ok(
        this.toResponse()
            .withOwnership(
                currentUserId = currentUserId,
                ownerId = this.ownerId
            )
    )
}