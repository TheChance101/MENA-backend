package net.thechance.faith.api.dto.prayertime

data class PrayerDateResponse(
    val hijri: PrayerHijriDate,
    val gregorian: PrayerGregorianDate,
)
