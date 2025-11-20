package net.thechance.trends.utils

import net.thechance.trends.api.dto.analytics.WatchTimeDto
import net.thechance.trends.entity.Trend
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID

object DummyTrends {
    val trend1 = Trend(
        id = UUID.randomUUID(),
        ownerId = DummyTrendUsers.user2.userId,
        videoUrl = "https://example.com/video1.mp4",
        description = "Tech trend",
        categories = mutableSetOf(DummyCategories.technology),
        isPublished = true
    )

    val trend2 = Trend(
        id = UUID.randomUUID(),
        ownerId = DummyTrendUsers.user2.userId,
        videoUrl = "https://example.com/video2.mp4",
        description = "Sports trend",
        categories = mutableSetOf(DummyCategories.sports),
        isPublished = true
    )

    val trend3 = Trend(
        id = UUID.randomUUID(),
        ownerId = DummyTrendUsers.user3.userId,
        videoUrl = "https://example.com/video3.mp4",
        description = "Multi category trend",
        categories = mutableSetOf(DummyCategories.technology, DummyCategories.sports, DummyCategories.nature),
        isPublished = true
    )

    val trend4 = Trend(
        id = UUID.randomUUID(),
        ownerId = DummyTrendUsers.user3.userId,
        videoUrl = "https://example.com/video4.mp4",
        description = "Fashion and science trend",
        categories = mutableSetOf(DummyCategories.fashion, DummyCategories.science),
        isPublished = true
    )
}

fun getWatchTimeDto(
    trendId: UUID,
    percentWatched: Double,
    watchStartTimeStamp: LocalDateTime = LocalDateTime.now(),
    watchEndTimeStamp: LocalDateTime = LocalDateTime.now(),
    videoDuration: Long = 1L,
): WatchTimeDto {
    return WatchTimeDto(
        trendId = trendId,
        percentWatched = percentWatched,
        watchStartTimeStamp = watchStartTimeStamp,
        watchEndTimeStamp = watchEndTimeStamp,
        videoDuration = videoDuration,
    )
}