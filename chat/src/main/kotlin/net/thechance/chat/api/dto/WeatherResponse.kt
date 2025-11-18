package net.thechance.chat.api.dto

data class WeatherResponse(
    val currentTemperature: Double,
    val minTemperature: Double,
    val maxTemperature: Double,
    val weatherCode: Int
)