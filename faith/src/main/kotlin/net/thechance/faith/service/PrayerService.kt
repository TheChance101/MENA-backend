package net.thechance.faith.service

import net.thechance.faith.entity.DayPrayerTimings
import net.thechance.faith.exception.FailedToGetPrayerTimesException
import net.thechance.faith.remote.PrayerRemoteClient
import net.thechance.faith.remote.dto.prayertime.toDayPrayerTimings
import net.thechance.faith.repository.PrayerRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

@Service
class PrayerService(
    private val prayerRepository: PrayerRepository,
    private val prayerRemoteClient: PrayerRemoteClient,
    private val logger: Logger = LoggerFactory.getLogger(PrayerService::class.java)
) {

    fun getPrayerTimes(latitude: Double, longitude: Double, date: LocalDate): DayPrayerTimings {
        return runCatching {

            val localPrayerTimes: DayPrayerTimings? = getLocalPrayerTimesIfNotExpired(
                latitude = latitude,
                longitude = longitude,
                date = date
            )
            if (localPrayerTimes != null) return localPrayerTimes

            val remotePrayerTimes = prayerRemoteClient.getPrayerTimes(latitude, longitude, date).toDayPrayerTimings(
                latitude = latitude,
                longitude = longitude
            )
            safeCachePrayerTimes(remotePrayerTimes)

            remotePrayerTimes
        }.getOrElse {
            throw FailedToGetPrayerTimesException(it.message.orEmpty())
        }
    }

    private fun getLocalPrayerTimesIfNotExpired(
        latitude: Double,
        longitude: Double,
        date: LocalDate
    ): DayPrayerTimings? = runCatching {
        val cachedPrayerTimes: DayPrayerTimings? =
            prayerRepository.findByLatitudeAndLongitudeAndDateSortedByNearestLocation(
                longitude = longitude,
                latitude = latitude,
                date = date
            ).firstOrNull()
        return if (cachedPrayerTimes != null && isDateExpired(cacheDate = cachedPrayerTimes.savedIn).not())
            cachedPrayerTimes
        else
            null
    }.getOrElse {
        logger.error("Error getting local prayer times", it)
        null
    }

    private fun safeCachePrayerTimes(prayerTimes: DayPrayerTimings) = runCatching {
        prayerRepository.save(prayerTimes)
    }

    private fun isDateExpired(cacheDate: Instant): Boolean {
        val today: LocalDate = LocalDate.now(ZONE_ID)
        val cacheLocalDate = cacheDate.atZone(ZONE_ID).toLocalDate()
        return cacheLocalDate.isBefore(today)
    }

    fun clearOldCache() {
        val today: LocalDate = LocalDate.now(ZONE_ID)
        val startOfToday = today.atStartOfDay(ZONE_ID).toInstant()
        prayerRepository.deleteOlderThan(startOfToday)
    }

    private companion object {
        val ZONE_ID: ZoneId = ZoneId.systemDefault()
    }
}
