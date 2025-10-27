package net.thechance.faith.api.controller

import net.thechance.faith.api.dto.prayertime.DayPrayerTimingsResponse
import net.thechance.faith.api.dto.prayertime.toResponse
import net.thechance.faith.exception.InvalidDateFormatException
import net.thechance.faith.service.PrayerService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.LocalDate

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

    private fun String.toLocalDate(): LocalDate = runCatching {
        LocalDate.parse(this)
    }.getOrElse { throw InvalidDateFormatException() }
}
