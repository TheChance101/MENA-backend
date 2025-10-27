package net.thechance.trends.api.dto.trend

import net.thechance.trends.entity.Trend
import net.thechance.trends.models.TrendWithOwnerShipAndLikeStatus
import java.time.LocalDateTime
import java.util.*

data class TrendResponse(
    val trendId: UUID,
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

fun Trend.toResponse(isLiked: Boolean = false): TrendResponse {
    return TrendResponse(
        trendId = id,
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

fun TrendWithOwnerShipAndLikeStatus.toResponse(): TrendResponse {
    return TrendResponse(
        trendId = trendId,
        thumbnailUrl = thumbnailUrl,
        videoUrl = videoUrl,
        description = description,
        createdAt = createdAt,
        likesCount = likesCount,
        viewsCount = viewsCount,
        isCurrentUserOwner = isCurrentUserOwner,
        isLiked = isLiked
    )
}