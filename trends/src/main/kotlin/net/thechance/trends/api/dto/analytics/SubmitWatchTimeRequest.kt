package net.thechance.trends.api.dto.analytics

import java.time.LocalDateTime
import java.util.UUID

data class SubmitWatchTimeRequest(
    val userId: UUID,
    val watchTimes: List<WatchTimeDto>,
)

data class WatchTimeDto(
    val trendId: UUID,
    val percentWatched: Double,
)
