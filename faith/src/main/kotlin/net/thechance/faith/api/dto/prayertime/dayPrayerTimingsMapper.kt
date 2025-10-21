package net.thechance.faith.api.dto.prayertime

import net.thechance.faith.entity.DayPrayerTimings

fun DayPrayerTimings.toResponse(): DayPrayerTimingsResponse = DayPrayerTimingsResponse(
    hijriDate = hijriDate,
    fajr = fajr,
    sunrise = sunrise,
    dhuhr = dhuhr,
    asr = asr,
    maghrib = maghrib,
    isha = isha
)
