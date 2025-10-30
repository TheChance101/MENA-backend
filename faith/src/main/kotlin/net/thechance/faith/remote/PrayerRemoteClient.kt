package net.thechance.faith.remote

import kotlinx.serialization.json.Json
import net.thechance.faith.exception.FailedToGetPrayerTimesException
import net.thechance.faith.remote.dto.prayertime.PrayerTimingsRemoteDto
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate
import org.springframework.web.util.UriComponentsBuilder
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Component
class PrayerRemoteClient(
    private val json: Json = Json { ignoreUnknownKeys = true },
    private val restTemplate: RestTemplate = RestTemplate(),
    private val logger: Logger = LoggerFactory.getLogger(PrayerRemoteClient::class.java)
) {

    fun getPrayerTimes(latitude: Double, longitude: Double, date: LocalDate): PrayerTimingsRemoteDto {
        return runCatching {
            val url = UriComponentsBuilder.fromHttpUrl(BASE_URL)
                .path("$PATH${date.formatAsDdMmYyyy()}")
                .queryParam(QUERY_LATITUDE, latitude)
                .queryParam(QUERY_LONGITUDE, longitude)
                .build()
                .toUriString()

            val rawJson = restTemplate.getForObject(url, String::class.java)
                ?: throw FailedToGetPrayerTimesException(
                    "failed to fetch prayer times from remote: response body is null"
                )

            json.decodeFromString(PrayerTimingsRemoteDto.serializer(), rawJson)
        }.getOrElse {
            logger.error("failed to get prayer times from remote: ex: $it")
            throw FailedToGetPrayerTimesException("failed to fetch prayer times from remote: $it")
        }
    }

    private fun LocalDate.formatAsDdMmYyyy(): String =
        this.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))

    private companion object {
        const val BASE_URL = "https://api.aladhan.com"
        const val PATH = "/v1/timings/"
        const val QUERY_LATITUDE = "latitude"
        const val QUERY_LONGITUDE = "longitude"
    }
}
