package net.thechance.trends.models

import java.time.LocalDateTime
import java.util.*

data class TrendWithOwnerShipAndLikeStatus(
    val trendId: UUID,
    val thumbnailUrl: String?,
    val videoUrl: String,
    val description: String,
    val createdAt: LocalDateTime,
    val likesCount: Int,
    val viewsCount: Int,
    val isLiked: Boolean,
    val isCurrentUserOwner: Boolean,
)

fun TrendWithLikeStatus.withOwnership(currentUserId: UUID): TrendWithOwnerShipAndLikeStatus {
    val trend = getTrend()
    val isLiked = getIsLiked()
    return TrendWithOwnerShipAndLikeStatus(
        trendId = trend.id,
        thumbnailUrl = trend.thumbnailUrl,
        videoUrl = trend.videoUrl,
        description = trend.description,
        createdAt = trend.createdAt,
        likesCount = trend.likesCount,
        viewsCount = trend.viewsCount,
        isLiked = isLiked,
        isCurrentUserOwner = currentUserId == trend.ownerId
    )
}