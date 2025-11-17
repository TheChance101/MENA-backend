package net.thechance.trends.api.dto.analytics

import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotEmpty
import java.time.LocalDateTime
import java.util.UUID

data class SubmitWatchTimeRequest(
    val userId: UUID,
    @field:NotEmpty val watchTimes: List<WatchTimeDto>,
)

data class WatchTimeDto(
    val trendId: UUID,
    val watchStartTimeStamp: LocalDateTime,
    val watchEndTimeStamp: LocalDateTime,
    val videoDuration: Long,
    @field:Min(0) val percentWatched: Double,
)
