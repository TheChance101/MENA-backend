package net.thechance.faith.api.dto.prayertime

import net.thechance.faith.entity.DayPrayerTimings

fun DayPrayerTimings.toResponse(): DayPrayerTimingsResponse = DayPrayerTimingsResponse(
    date = PrayerDateResponse(
        hijri = PrayerHijriDate(
            date = hijriDate,
            readableDate = hijriReadableDate,
            day = hijriDay,
            dayName = hijriDayName,
            dayArabicName = hijriDayArabicName,
            month = hijriMonth,
            monthName = hijriMonthName,
            monthArabicName = hijriMonthArabicName,
            year = hijriYear
        ),
        gregorian = PrayerGregorianDate(
            date = gregorianDate,
            timestamp = dateTimestamp,
            readableDate = gregorianReadableDate,
            day = gregorianDay,
            dayName = gregorianDayName,
            month = gregorianMonth,
            monthName = gregorianMonthName,
            year = gregorianYear
        )
    ),
    fajr = fajr,
    sunrise = sunrise,
    dhuhr = dhuhr,
    asr = asr,
    maghrib = maghrib,
    isha = isha
)
