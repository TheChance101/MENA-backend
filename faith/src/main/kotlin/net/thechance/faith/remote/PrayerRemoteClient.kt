package net.thechance.faith.remote

import kotlinx.serialization.json.Json
import net.thechance.faith.api.controller.exception.CannotGetPrayerTimesException
import net.thechance.faith.remote.dto.PrayerTimingsRemoteDto
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient

@Component
class PrayerRemoteClient(
    private val json: Json = Json { ignoreUnknownKeys = true }
) {

    private val webClient: WebClient = WebClient
        .builder()
        .baseUrl(BASE_URL)
        .build()

    fun getPrayerTimes(latitude: Double, longitude: Double, date: String): PrayerTimingsRemoteDto = runCatching {
        val rawJson = webClient.get()
            .uri { uriBuilder ->
                uriBuilder
                    .path("$PATH$date")
                    .queryParam(QUERY_LATITUDE, latitude)
                    .queryParam(QUERY_LONGITUDE, longitude)
                    .build()
            }
            .retrieve()
            .bodyToMono(String::class.java)
            .block() ?: throw CannotGetPrayerTimesException()
        json.decodeFromString(PrayerTimingsRemoteDto.serializer(), rawJson)
    }.getOrElse {
        throw CannotGetPrayerTimesException()
    }

    private companion object {
        const val BASE_URL = "https://api.aladhan.com"
        const val PATH = "/v1/timings/"
        const val QUERY_LATITUDE = "latitude"
        const val QUERY_LONGITUDE = "longitude"
    }
}
