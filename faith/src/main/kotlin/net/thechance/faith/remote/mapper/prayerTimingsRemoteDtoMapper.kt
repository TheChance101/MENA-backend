package net.thechance.faith.remote.mapper

import net.thechance.faith.entity.DayPrayerTimings
import net.thechance.faith.remote.dto.PrayerTimingsRemoteDto

fun PrayerTimingsRemoteDto.DayPrayerTimings(latitude: Double, longitude: Double): DayPrayerTimings =
    DayPrayerTimings(
        id = 0,
        latitude = latitude,
        longitude = longitude,
        gregorianDate = data.date.gregorian.date,
        dateTimestamp = data.date.timestamp,
        gregorianReadableDate = data.date.readable,
        gregorianDay = data.date.gregorian.day,
        gregorianDayName = data.date.gregorian.weekday.en,
        gregorianMonth = data.date.gregorian.month.number,
        gregorianMonthName = data.date.gregorian.month.en,
        gregorianYear = data.date.gregorian.year,
        hijriDate = data.date.hijri.date,
        hijriReadableDate = hijriDateToReadable(
            day = data.date.hijri.day,
            month = data.date.hijri.month.en,
            year = data.date.hijri.year
        ),
        hijriDay = data.date.hijri.day,
        hijriDayName = data.date.hijri.weekday.en,
        hijriDayArabicName = data.date.hijri.weekday.ar,
        hijriMonth = data.date.hijri.month.number,
        hijriYear = data.date.hijri.year,
        hijriMonthName = data.date.hijri.month.en,
        hijriMonthArabicName = data.date.hijri.month.ar,
        fajr = data.timings.fajr,
        sunrise = data.timings.sunrise,
        dhuhr = data.timings.dhuhr,
        asr = data.timings.asr,
        maghrib = data.timings.maghrib,
        isha = data.timings.isha
    )

private fun hijriDateToReadable(
    day: String,
    month: String,
    year: String
): String = "$day $month $year"
