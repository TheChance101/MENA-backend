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
        .baseUrl("https://api.aladhan.com")
        .build()

    fun getPrayerTimes(latitude: Double, longitude: Double, date: String): PrayerTimingsRemoteDto = runCatching {
        val rawJson = webClient.get()
            .uri { uriBuilder ->
                uriBuilder
                    .path("/v1/timings/$date")
                    .queryParam("latitude", latitude)
                    .queryParam("longitude", longitude)
                    .build()
            }
            .retrieve()
            .bodyToMono(String::class.java)
            .block() ?: throw CannotGetPrayerTimesException()
        json.decodeFromString(PrayerTimingsRemoteDto.serializer(), rawJson)
    }.getOrElse {
        throw CannotGetPrayerTimesException()
    }
}
