package net.thechance.faith.api.dto.prayertime

data class DayPrayerTimingsResponse(
    val fajr: String,
    val sunrise: String,
    val dhuhr: String,
    val asr: String,
    val maghrib: String,
    val isha: String,
    val date: PrayerDateResponse
)
