package net.thechance.faith.api.dto.prayertime

import java.time.Instant
import java.time.LocalDate

data class DayPrayerTimingsResponse(
    val date: LocalDate,
    val hijriDate: String,
    val fajr: Instant,
    val sunrise: Instant,
    val dhuhr: Instant,
    val asr: Instant,
    val maghrib: Instant,
    val isha: Instant,
)
