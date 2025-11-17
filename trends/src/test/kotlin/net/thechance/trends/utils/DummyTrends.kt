package net.thechance.trends.utils

import net.thechance.trends.entity.Trend
import java.util.UUID

object DummyTrends {
    val trend1 = Trend(
        id = UUID.fromString("10000000-0000-0000-0000-000000000001"),
        ownerId = DummyTrendUsers.user2.userId,
        videoUrl = "https://example.com/video1.mp4",
        description = "Tech trend",
        categories = mutableSetOf(DummyCategories.technology),
        isPublished = true
    )

    val trend2 = Trend(
        id = UUID.fromString("10000000-0000-0000-0000-000000000002"),
        ownerId = DummyTrendUsers.user2.userId,
        videoUrl = "https://example.com/video2.mp4",
        description = "Sports trend",
        categories = mutableSetOf(DummyCategories.sports),
        isPublished = true
    )

    val trend3 = Trend(
        id = UUID.fromString("10000000-0000-0000-0000-000000000003"),
        ownerId = DummyTrendUsers.user3.userId,
        videoUrl = "https://example.com/video3.mp4",
        description = "Multi category trend",
        categories = mutableSetOf(DummyCategories.technology, DummyCategories.sports, DummyCategories.nature),
        isPublished = true
    )

    val trend4 = Trend(
        id = UUID.fromString("10000000-0000-0000-0000-000000000004"),
        ownerId = DummyTrendUsers.user3.userId,
        videoUrl = "https://example.com/video4.mp4",
        description = "Fashion and science trend",
        categories = mutableSetOf(DummyCategories.fashion, DummyCategories.science),
        isPublished = true
    )
}