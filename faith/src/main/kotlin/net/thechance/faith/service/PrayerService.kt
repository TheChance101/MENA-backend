package net.thechance.faith.service

import net.thechance.faith.api.controller.exception.CannotGetPrayerTimesException
import net.thechance.faith.entity.DayPrayerTimings
import net.thechance.faith.remote.PrayerRemoteClient
import net.thechance.faith.remote.mapper.toDayPrayerTimings
import net.thechance.faith.repository.PrayerRepository
import net.thechance.faith.utils.orZero
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

@Service
class PrayerService(
    private val prayerRepository: PrayerRepository,
    private val prayerRemoteClient: PrayerRemoteClient
) {

    private val today: LocalDate = LocalDate.now(ZoneId.systemDefault())

    fun getPrayerTimes(latitude: Double, longitude: Double, date: String): DayPrayerTimings {

        return runCatching {

            val nearestCashedLocalPrayerTimes: DayPrayerTimings? =
                prayerRepository.findByLatitudeAndLongitudeAndDateSortedByNearestLocation(
                    longitude = longitude,
                    latitude = latitude,
                    date = date.toLocalDate()
                )?.firstOrNull()

            if (nearestCashedLocalPrayerTimes != null && isDateExpired(cacheDate = nearestCashedLocalPrayerTimes.savedIn).not())
                return nearestCashedLocalPrayerTimes

            val remotePrayerTimes = prayerRemoteClient.getPrayerTimes(latitude, longitude, date).toDayPrayerTimings(
                latitude = latitude,
                longitude = longitude
            )

            runCatching { prayerRepository.save(remotePrayerTimes) }
            remotePrayerTimes
        }.getOrElse {
            throw CannotGetPrayerTimesException(it.message.orEmpty())
        }
    }

    private fun isDateExpired(cacheDate: Instant): Boolean {
        val cacheLocalDate = cacheDate.atZone(ZoneId.systemDefault()).toLocalDate()
        return cacheLocalDate.isBefore(today)
    }

    fun String.toLocalDate(): LocalDate = runCatching {
        this.split('-').let {
            val day = it[0].toIntOrNull().orZero()
            val month = it[1].toIntOrNull().orZero()
            val year = it[2].toIntOrNull().orZero()
            LocalDate.of(year, month, day)

        }
    }.getOrDefault(LocalDate.of(1970, 1, 1))

    @Scheduled(cron = "0 0 0 * * *")
    fun clearOldCache() {
        val startOfToday = today.atStartOfDay(ZoneId.systemDefault()).toInstant()
        prayerRepository.deleteOlderThan(startOfToday)
    }
}
