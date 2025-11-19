package net.thechance.chat.repository.weather

import net.thechance.chat.service.model.WeatherModel

interface WeatherRepository {
    fun getCurrentWeather(latitude: Double, longitude: Double): WeatherModel
}