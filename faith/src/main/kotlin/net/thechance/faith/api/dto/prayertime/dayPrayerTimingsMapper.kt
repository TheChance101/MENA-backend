package net.thechance.faith.api.dto.prayertime

import net.thechance.faith.entity.DayPrayerTimings
import java.time.chrono.HijrahDate
import java.time.format.DateTimeFormatter

fun DayPrayerTimings.toResponse(): DayPrayerTimingsResponse = DayPrayerTimingsResponse(
    date = date,
    hijriDate = HijrahDate.from(date).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
    fajr = fajr,
    sunrise = sunrise,
    dhuhr = dhuhr,
    asr = asr,
    maghrib = maghrib,
    isha = isha
)
