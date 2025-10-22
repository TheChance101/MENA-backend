package net.thechance.faith.api.dto.prayertime

data class PrayerGregorianDate(
    val date: String,
    val dateFormat: String = "dd-MM-YYYY",
    val timestamp: String,
    val readableDate: String,
    val day: String,
    val dayName: String,
    val month: Int,
    val monthName: String,
    val year: String,
)
