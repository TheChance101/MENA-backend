package net.thechance.chat.api.dto

import net.thechance.chat.service.model.WeatherModel

data class WeatherResponse(
    val currentTemperature: Double,
    val minTemperature: Double,
    val maxTemperature: Double,
    val weatherType: String
)

fun WeatherModel.toResponse() = WeatherResponse(
    currentTemperature = currentTemperature,
    minTemperature = minTemperature,
    maxTemperature = maxTemperature,
    weatherType = weatherType.name
)