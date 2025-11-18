package net.thechance.chat.repository.weather

import net.thechance.chat.api.dto.WeatherResponse
import net.thechance.chat.service.exception.FetchWeatherException
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.stereotype.Repository
import org.springframework.web.client.RestClientResponseException
import org.springframework.web.client.getForEntity

@Repository
class WeatherRepositoryImp(
    restTemplateBuilder: RestTemplateBuilder
) : WeatherRepository {

    private val restTemplate = restTemplateBuilder.build()

    override fun getCurrentWeather(latitude: Double, longitude: Double): WeatherResponse {
        val url = (
            "https://api.open-meteo.com/v1/forecast" +
                "?latitude=$latitude&longitude=$longitude" +
                "&daily=temperature_2m_max,temperature_2m_min" +
                "&current=temperature_2m,weather_code" +
                "&timezone=auto" +
                "&forecast_days=1"
            )

        return makeRequest(url).toResponse()
    }

    private fun makeRequest(url: String): WeatherDTO {
        try {
            val response = restTemplate.getForEntity<WeatherDTO>(url)

            if (!response.statusCode.is2xxSuccessful) {
                throw FetchWeatherException("Weather API error: ${response.statusCode}")
            }

            return response.body ?: throw FetchWeatherException("Empty weather response")

        } catch (e: RestClientResponseException) {
            throw FetchWeatherException("Weather API error: ${e.statusCode}", e)
        }
    }
}

private fun WeatherDTO.toResponse(): WeatherResponse = WeatherResponse(
    currentTemperature = current?.temperature ?: 0.0,
    minTemperature = daily?.minTemperature?.firstOrNull() ?: 0.0,
    maxTemperature = daily?.maxTemperature?.firstOrNull() ?: 0.0,
    weatherCode = current?.weatherCode ?: 0
)