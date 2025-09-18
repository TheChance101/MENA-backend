package net.thechance.trends.api.dto

import jakarta.persistence.Column
import net.thechance.trends.entity.Category
import net.thechance.trends.entity.Reel
import java.time.LocalDateTime
import java.util.*

data class ReelResponse(
    @Column(name = "reel_id")
    val reelId: UUID,
    @Column(name = "thumbnail_url")
    val thumbnailUrl: String,
    @Column(name = "video_url")
    val videoUrl: String,
    @Column(name = "description")
    val description: String,
    @Column(name = "created_at")
    val createdAt: LocalDateTime,
    @Column(name = "likes_count")
    val likesCount: Int,
    @Column(name = "views_count")
    val viewsCount: Int,
    @Column(name = "categories")
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


fun List<Reel>.toResponseList() = map { it.toResponse() }