package net.thechance.faith.schedulers

import net.thechance.faith.service.PrayerService
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class PrayerCacheScheduler(private val prayerService: PrayerService) {

    @Scheduled(cron = "0 0 0 * * *")
    fun clearOldPrayerCache() {
        prayerService.clearOldCache()
    }
}
