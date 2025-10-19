package net.thechance.faith.remote.mapper

import net.thechance.faith.entity.DayPrayerTimings
import net.thechance.faith.remote.dto.PrayerTimingsRemoteDto
import java.time.LocalDate
import java.time.ZoneId

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
        fajr = data?.timings?.fajr.toInstant(startOfDay),
        sunrise = data?.timings?.sunrise.toInstant(startOfDay),
        dhuhr = data?.timings?.dhuhr.toInstant(startOfDay),
        asr = data?.timings?.asr.toInstant(startOfDay),
        maghrib = data?.timings?.maghrib.toInstant(startOfDay),
        isha = data?.timings?.isha.toInstant(startOfDay),
    )
}
