package net.thechance.faith.remote

import kotlinx.serialization.json.Json
import net.thechance.faith.exception.FailedToGetPrayerTimesException
import net.thechance.faith.remote.dto.PrayerTimingsRemoteDto
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate
import org.springframework.web.util.UriComponentsBuilder

@Component
class PrayerRemoteClient(
    private val json: Json = Json { ignoreUnknownKeys = true }
) {
    private val restTemplate = RestTemplate()

    fun getPrayerTimes(latitude: Double, longitude: Double, date: String): PrayerTimingsRemoteDto {
        return runCatching {
            val url = UriComponentsBuilder.fromHttpUrl(BASE_URL)
                .path("$PATH$date")
                .queryParam(QUERY_LATITUDE, latitude)
                .queryParam(QUERY_LONGITUDE, longitude)
                .build()
                .toUriString()

            val rawJson = restTemplate.getForObject(url, String::class.java)
                ?: throw FailedToGetPrayerTimesException(
                    "failed to fetch prayer times from remote: response body is null"
                )

            json.decodeFromString(PrayerTimingsRemoteDto.serializer(), rawJson)
        }.getOrElse { throw FailedToGetPrayerTimesException("failed to fetch prayer times from remote") }
    }

    private companion object {
        const val BASE_URL = "https://api.aladhan.com"
        const val PATH = "/v1/timings/"
        const val QUERY_LATITUDE = "latitude"
        const val QUERY_LONGITUDE = "longitude"
    }
}
