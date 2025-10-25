package net.thechance.trends.api.dto.reel

import net.thechance.trends.entity.Reel
import net.thechance.trends.models.ReelWithLikeStatus
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

fun ReelWithLikeStatus.toResponse(): ReelResponse {
    val reel = getReel()
    val isLiked = getIsLiked()
    return ReelResponse(
        reelId = reel.id,
        thumbnailUrl = reel.thumbnailUrl,
        videoUrl = reel.videoUrl,
        description = reel.description,
        createdAt = reel.createdAt,
        likesCount = reel.likesCount,
        viewsCount = reel.viewsCount,
        isCurrentUserOwner = false,
        isLiked = isLiked
    )
}