package net.thechance.chat.api.controller

import net.thechance.chat.api.dto.WeatherResponse
import net.thechance.chat.api.dto.toResponse
import net.thechance.chat.repository.weather.WeatherRepository
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/weather")
class WeatherController(
    private val weatherRepository: WeatherRepository,
) {

    @GetMapping("/current")
    fun getCurrentWeather(
        @RequestParam("lat") latitude: Double,
        @RequestParam("lng") longitude: Double
    ): ResponseEntity<WeatherResponse> {
        val weather = weatherRepository.getCurrentWeather(latitude, longitude).toResponse()
        return ResponseEntity.ok(weather)
    }
}
