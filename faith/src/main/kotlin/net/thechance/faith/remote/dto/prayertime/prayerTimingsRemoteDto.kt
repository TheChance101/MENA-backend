package net.thechance.faith.remote.dto.prayertime

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PrayerTimingsRemoteDto(
    @SerialName("code")
    val code: Int?,
    @SerialName("status")
    val status: String?,
    @SerialName("data")
    val data: PrayerDataRemoteDto?
)

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
    @SerialName("Maghrib")
    val maghrib: String?,
    @SerialName("Isha")
    val isha: String?,
)

@Serializable
data class DateInfoRemoteDto(
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
)

@Serializable
data class GregorianRemoteDto(
    @SerialName("date")
    val date: String?,
    @SerialName("format")
    val format: String?,
    @SerialName("day")
    val day: String?,
    @SerialName("month")
    val month: MonthGregorianRemoteDto?,
    @SerialName("year")
    val year: String?,
)

@Serializable
data class MonthGregorianRemoteDto(
    @SerialName("number")
    val number: Int?,
)

@Serializable
data class MetaRemoteDto(
    @SerialName("timezone")
    val timezone: String?,
)
