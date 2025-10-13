package net.thechance.faith.remote.mapper

import net.thechance.faith.entity.DayPrayerTimings
import net.thechance.faith.remote.dto.PrayerTimingsRemoteDto

fun PrayerTimingsRemoteDto.DayPrayerTimings(latitude: Double, longitude: Double): DayPrayerTimings =
    DayPrayerTimings(
        id = 0,
        latitude = latitude,
        longitude = longitude,
        gregorianDate = data?.date?.gregorian?.date.orEmpty(),
        dateTimestamp = data?.date?.timestamp.orEmpty(),
        gregorianReadableDate = data?.date?.readable.orEmpty(),
        gregorianDay = data?.date?.gregorian?.day.orEmpty(),
        gregorianDayName = data?.date?.gregorian?.weekday?.en.orEmpty(),
        gregorianMonth = data?.date?.gregorian?.month?.number.orZero(),
        gregorianMonthName = data?.date?.gregorian?.month?.en.orEmpty(),
        gregorianYear = data?.date?.gregorian?.year.orEmpty(),
        hijriDate = data?.date?.hijri?.date.orEmpty(),
        hijriReadableDate = hijriDateToReadable(
            day = data?.date?.hijri?.day.orEmpty(),
            month = data?.date?.hijri?.month?.en.orEmpty(),
            year = data?.date?.hijri?.year.orEmpty()
        ),
        hijriDay = data?.date?.hijri?.day.orEmpty(),
        hijriDayName = data?.date?.hijri?.weekday?.en.orEmpty(),
        hijriDayArabicName = data?.date?.hijri?.weekday?.ar.orEmpty(),
        hijriMonth = data?.date?.hijri?.month?.number.orZero(),
        hijriYear = data?.date?.hijri?.year.orEmpty(),
        hijriMonthName = data?.date?.hijri?.month?.en.orEmpty(),
        hijriMonthArabicName = data?.date?.hijri?.month?.ar.orEmpty(),
        fajr = data?.timings?.fajr.orEmpty(),
        sunrise = data?.timings?.sunrise.orEmpty(),
        dhuhr = data?.timings?.dhuhr.orEmpty(),
        asr = data?.timings?.asr.orEmpty(),
        maghrib = data?.timings?.maghrib.orEmpty(),
        isha = data?.timings?.isha.orEmpty()
    )

private fun hijriDateToReadable(
    day: String,
    month: String,
    year: String
): String = "$day $month $year"

fun Int?.orZero(): Int = this ?: 0
