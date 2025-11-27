package net.thechance.chat.repository.weather

import com.fasterxml.jackson.annotation.JsonProperty

data class WeatherDTO(
    val current: CurrentDTO? = null,
    val daily: DailyDTO? = null
) {
    data class CurrentDTO(
        @param:JsonProperty("temperature_2m")
        val temperature: Double? = null,
        @param:JsonProperty("weather_code")
        val weatherCode: Int? = null
    )

    data class DailyDTO(
        @param:JsonProperty("temperature_2m_min")
        val minTemperature: List<Double>? = null,
        @param:JsonProperty("temperature_2m_max")
        val maxTemperature: List<Double>? = null
    )
}

