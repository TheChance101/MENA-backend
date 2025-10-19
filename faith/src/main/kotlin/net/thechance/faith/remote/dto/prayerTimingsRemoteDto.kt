package net.thechance.faith.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import net.thechance.faith.remote.mapper.StringHoursAndMinutesToInstantMapper

@Serializable
data class PrayerTimingsRemoteDto(
    @SerialName("code")
    val code: Int?,
    @SerialName("status")
    val status: String?,
    @SerialName("data")
    val data: PrayerDataRemoteDto?
) : StringHoursAndMinutesToInstantMapper

@Serializable
data class PrayerDataRemoteDto(
    @SerialName("timings")
    val timings: TimingsRemoteDto?,
    @SerialName("date")
    val date: DateInfoRemoteDto?,
    @SerialName("meta")
    val meta: MetaRemoteDto?
)

@Serializable
data class TimingsRemoteDto(
    @SerialName("Fajr")
    val fajr: String?,
    @SerialName("Sunrise")
    val sunrise: String?,
    @SerialName("Dhuhr")
    val dhuhr: String?,
    @SerialName("Asr")
    val asr: String?,
    @SerialName("Sunset")
    val sunset: String?,
    @SerialName("Maghrib")
    val maghrib: String?,
    @SerialName("Isha")
    val isha: String?,
    @SerialName("Imsak")
    val imsak: String?,
    @SerialName("Midnight")
    val midnight: String?,
    @SerialName("Firstthird")
    val firstThird: String?,
    @SerialName("Lastthird")
    val lastThird: String?
)

@Serializable
data class DateInfoRemoteDto(
    @SerialName("readable")
    val readable: String?,
    @SerialName("timestamp")
    val timestamp: String?,
    @SerialName("hijri")
    val hijri: HijriRemoteDto?,
    @SerialName("gregorian")
    val gregorian: GregorianRemoteDto?
)

@Serializable
data class HijriRemoteDto(
    @SerialName("date")
    val date: String?,
    @SerialName("format")
    val format: String?,
    @SerialName("day")
    val day: String?,
    @SerialName("weekday")
    val weekday: WeekdayHijriRemoteDto?,
    @SerialName("month")
    val month: MonthHijriRemoteDto?,
    @SerialName("year")
    val year: String?,
    @SerialName("designation")
    val designation: DesignationRemoteDto?,
    @SerialName("holidays")
    val holidays: List<String>?,
    @SerialName("adjustedHolidays")
    val adjustedHolidays: List<String>?,
    @SerialName("method")
    val method: String?
)

@Serializable
data class WeekdayHijriRemoteDto(
    @SerialName("en")
    val en: String?,
    @SerialName("ar")
    val ar: String?
)

@Serializable
data class MonthHijriRemoteDto(
    @SerialName("number")
    val number: Int?,
    @SerialName("en")
    val en: String?,
    @SerialName("ar")
    val ar: String?,
    @SerialName("days")
    val days: Int?
)

@Serializable
data class GregorianRemoteDto(
    @SerialName("date")
    val date: String?,
    @SerialName("format")
    val format: String?,
    @SerialName("day")
    val day: String?,
    @SerialName("weekday")
    val weekday: WeekdayGregorianRemoteDto?,
    @SerialName("month")
    val month: MonthGregorianRemoteDto?,
    @SerialName("year")
    val year: String?,
    @SerialName("designation")
    val designation: DesignationRemoteDto?,
    @SerialName("lunarSighting")
    val lunarSighting: Boolean?
)

@Serializable
data class WeekdayGregorianRemoteDto(
    @SerialName("en")
    val en: String?
)

@Serializable
data class MonthGregorianRemoteDto(
    @SerialName("number")
    val number: Int?,
    @SerialName("en")
    val en: String?
)

@Serializable
data class DesignationRemoteDto(
    @SerialName("abbreviated")
    val abbreviated: String?,
    @SerialName("expanded")
    val expanded: String?
)

@Serializable
data class MetaRemoteDto(
    @SerialName("latitude")
    val latitude: Double?,
    @SerialName("longitude")
    val longitude: Double?,
    @SerialName("timezone")
    val timezone: String?,
    @SerialName("method")
    val method: MethodRemoteDto?,
    @SerialName("latitudeAdjustmentMethod")
    val latitudeAdjustmentMethod: String?,
    @SerialName("midnightMode")
    val midnightMode: String?,
    @SerialName("school")
    val school: String?,
    @SerialName("offset")
    val offset: OffsetRemoteDto?
)

@Serializable
data class MethodRemoteDto(
    @SerialName("id")
    val id: Int?,
    @SerialName("name")
    val name: String?,
    @SerialName("params")
    val params: MethodParamsRemoteDto?,
    @SerialName("location")
    val location: LocationRemoteDto?
)

@Serializable
data class MethodParamsRemoteDto(
    @SerialName("Fajr")
    val fajr: Double?,
    @SerialName("Isha")
    val isha: Double?
)

@Serializable
data class LocationRemoteDto(
    @SerialName("latitude")
    val latitude: Double?,
    @SerialName("longitude")
    val longitude: Double?
)

@Serializable
data class OffsetRemoteDto(
    @SerialName("Imsak")
    val imsak: Int?,
    @SerialName("Fajr")
    val fajr: Int?,
    @SerialName("Sunrise")
    val sunrise: Int?,
    @SerialName("Dhuhr")
    val dhuhr: Int?,
    @SerialName("Asr")
    val asr: Int?,
    @SerialName("Sunset")
    val sunset: Int?,
    @SerialName("Maghrib")
    val maghrib: Int?,
    @SerialName("Isha")
    val isha: Int?,
    @SerialName("Midnight")
    val midnight: Int?
)
