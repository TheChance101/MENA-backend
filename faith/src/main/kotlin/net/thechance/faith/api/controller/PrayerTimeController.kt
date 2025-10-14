package net.thechance.faith.api.controller

import net.thechance.faith.api.dto.prayertime.DayPrayerTimingsResponse
import net.thechance.faith.api.dto.prayertime.toResponse
import net.thechance.faith.service.PrayerService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("faith/DayPrayerTimes")
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
            date = date
        ).toResponse()
        return ResponseEntity.ok(prayerTimes)
    }
}
