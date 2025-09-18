package net.thechance.trends.api.dto

import net.thechance.trends.entity.Category
import net.thechance.trends.entity.Reel
import java.time.LocalDateTime
import java.util.*

data class ReelResponse(
    val reelId: UUID,
    val thumbnailUrl: String,
    val videoUrl: String,
    val description: String,
    val createdAt: LocalDateTime,
    val likesCount: Int,
    val viewsCount: Int,
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
        categories = categories
    )
}


fun List<Reel>.toResponse() = map { it.toResponse() }