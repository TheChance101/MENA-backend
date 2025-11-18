package net.thechance.chat.repository.weather

import net.thechance.chat.api.dto.WeatherResponse

interface WeatherRepository {
    fun getCurrentWeather(latitude: Double, longitude: Double): WeatherResponse
}