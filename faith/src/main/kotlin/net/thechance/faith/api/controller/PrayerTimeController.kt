package net.thechance.faith.api.controller

import net.thechance.faith.api.dto.prayertime.DayPrayerTimingsResponse
import net.thechance.faith.api.dto.prayertime.toResponse
import net.thechance.faith.service.PrayerService
import net.thechance.faith.utils.orZero
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
        this.split('-').let {
            val day = it[0].toIntOrNull().orZero()
            val month = it[1].toIntOrNull().orZero()
            val year = it[2].toIntOrNull().orZero()
            LocalDate.of(year, month, day)

        }
    }.getOrDefault(LocalDate.of(1970, 1, 1))
}
