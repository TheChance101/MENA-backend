package net.thechance.faith.remote.dto.prayertime

import net.thechance.faith.entity.DayPrayerTimings
import net.thechance.faith.utils.orZero
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit

fun PrayerTimingsRemoteDto.toDayPrayerTimings(latitude: Double, longitude: Double): DayPrayerTimings {

    val day = data?.date?.gregorian?.day?.toIntOrNull() ?: 1
    val month = data?.date?.gregorian?.month?.number ?: 1
    val year = data?.date?.gregorian?.year?.toIntOrNull() ?: 1970
    val localDate = LocalDate.of(year, month, day)
    val startOfDay = localDate.atStartOfDay(ZoneId.of(data?.meta?.timezone ?: "UTC")).toInstant()

    return DayPrayerTimings(
        id = 0,
        latitude = latitude,
        longitude = longitude,
        date = localDate,
        hijriDate = data?.date?.hijri?.date.orEmpty(),
        fajr = data?.timings?.fajr.toInstant(startOfDay),
        sunrise = data?.timings?.sunrise.toInstant(startOfDay),
        dhuhr = data?.timings?.dhuhr.toInstant(startOfDay),
        asr = data?.timings?.asr.toInstant(startOfDay),
        maghrib = data?.timings?.maghrib.toInstant(startOfDay),
        isha = data?.timings?.isha.toInstant(startOfDay),
    )
}

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