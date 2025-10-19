package net.thechance.faith.remote.mapper

import net.thechance.faith.utils.orZero
import java.time.Instant
import java.time.temporal.ChronoUnit

interface StringHoursAndMinutesToInstantMapper {

    fun String?.toInstant(
        startOfDay: Instant,
    ): Instant = runCatching {
        stringTimeToInstant(
            hoursAndMinutes = this,
            startOfDay = startOfDay,
        )
    }.getOrDefault(startOfDay)

    private fun stringTimeToInstant(
        hoursAndMinutes: String?,
        startOfDay: Instant,
    ): Instant {
        val parts = hoursAndMinutes?.split(":").orEmpty()
        if (parts.size < 2) return startOfDay
        val hour = parts[0].toIntOrNull().orZero()
        val minute = parts[1].toIntOrNull().orZero()

        return startOfDay
            .plus(hour.toLong(), ChronoUnit.HOURS)
            .plus(minute.toLong(), ChronoUnit.MINUTES)
    }
}
