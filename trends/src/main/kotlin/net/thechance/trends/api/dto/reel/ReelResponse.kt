package net.thechance.trends.api.dto.reel

import net.thechance.trends.entity.Reel
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
    val isLiked: Boolean,
    val isCurrentUserOwner: Boolean,
    val username: String = "The Chance",
    val profilePictureUrl: String = "",
)

fun Reel.toResponse(isLiked: Boolean = false): ReelResponse {
    return ReelResponse(
        reelId = id,
        thumbnailUrl = thumbnailUrl,
        videoUrl = videoUrl,
        description = description,
        createdAt = createdAt,
        likesCount = likesCount,
        viewsCount = viewsCount,
        isCurrentUserOwner = false,
        isLiked = isLiked
    )
}

fun ReelResponse.withOwnership(currentUserId: UUID, ownerId: UUID): ReelResponse {
    return this.copy(isCurrentUserOwner = currentUserId == ownerId)
}