package net.thechance.faith.service

import net.thechance.faith.api.controller.exception.CannotGetPrayerTimesException
import net.thechance.faith.entity.DayPrayerTimings
import net.thechance.faith.remote.PrayerRemoteClient
import net.thechance.faith.remote.mapper.DayPrayerTimings
import net.thechance.faith.repository.PrayerRepository
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
        val entity = runCatching {
            val cached = prayerRepository.findByLatitudeAndLongitudeAndGregorianDate(
                longitude = longitude,
                latitude = latitude,
                date = date
            )
            if (cached != null && isDateExpired(cacheDate = cached.savedIn).not()) return cached
            val remoteData = prayerRemoteClient.getPrayerTimes(latitude, longitude, date)
            remoteData.DayPrayerTimings(latitude, longitude)
        }.getOrElse { throw CannotGetPrayerTimesException("Failed to get prayer times") }

        runCatching { prayerRepository.save(entity) }
        return entity
    }

    private fun isDateExpired(cacheDate: Instant): Boolean {
        val cacheLocalDate = cacheDate.atZone(ZoneId.systemDefault()).toLocalDate()
        return cacheLocalDate.isBefore(today)
    }

    @Scheduled(cron = "0 0 0 * * *")
    fun clearOldCache() {
        val startOfToday = today.atStartOfDay(ZoneId.systemDefault()).toInstant()
        prayerRepository.deleteOlderThan(startOfToday)
    }
}
