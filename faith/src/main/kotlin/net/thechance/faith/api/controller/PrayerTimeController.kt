package net.thechance.faith.api.controller

import net.thechance.faith.api.dto.prayertime.DayPrayerTimingsResponse
import net.thechance.faith.api.dto.prayertime.toResponse
import net.thechance.faith.exception.InvalidDateFormatException
import net.thechance.faith.service.PrayerService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.LocalDate
import java.time.chrono.HijrahDate

@RestController
@RequestMapping("faith/prayer/times")
class PrayerTimeController(
    private val prayerService: PrayerService
) {

    @GetMapping("/{date}")
    fun getPrayerTimes(
        @PathVariable date: String,
        @RequestParam latitude: Double,
        @RequestParam longitude: Double
    ): ResponseEntity<DayPrayerTimingsResponse> {
        val prayerTimes = prayerService.getPrayerTimes(
            latitude = latitude,
            longitude = longitude,
            date = date.toLocalDate()
        ).toResponse()
        return ResponseEntity.ok(prayerTimes)
    }

    @GetMapping("hijri/{date}")
    fun getPrayerTimesByHijri(
        @PathVariable date: String,
        @RequestParam latitude: Double,
        @RequestParam longitude: Double
    ): ResponseEntity<DayPrayerTimingsResponse> {
        val prayerTimes = prayerService.getPrayerTimes(
            latitude = latitude,
            longitude = longitude,
            date = date.hijriDateToLocalDate()
        ).toResponse()
        return ResponseEntity.ok(prayerTimes)
    }

    private fun String.hijriDateToLocalDate(): LocalDate = runCatching {
        val parts = this.split("-")
        val year = parts[0].toInt()
        val month = parts[1].toInt()
        val day = parts[2].toInt()
        val hijriDate = HijrahDate.of(year, month, day)
        LocalDate.from(hijriDate)
    }.getOrElse { throw InvalidDateFormatException() }

    private fun String.toLocalDate(): LocalDate = runCatching {
        LocalDate.parse(this)
    }.getOrElse { throw InvalidDateFormatException() }
}
