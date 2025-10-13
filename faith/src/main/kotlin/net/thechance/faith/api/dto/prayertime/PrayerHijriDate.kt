package net.thechance.faith.api.dto.prayertime

data class PrayerHijriDate(
    val date: String,
    val dateFormat: String = "dd-MM-YYYY",
    val readableDate: String,
    val day: String,
    val dayName: String,
    val dayArabicName: String,
    val month: Int,
    val monthName: String,
    val monthArabicName: String,
    val year: String,
)
