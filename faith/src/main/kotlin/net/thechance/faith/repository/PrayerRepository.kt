package net.thechance.faith.repository

import jakarta.transaction.Transactional
import net.thechance.faith.entity.DayPrayerTimings
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import java.time.Instant

interface PrayerRepository : JpaRepository<DayPrayerTimings, Int> {

    fun findByLatitudeAndLongitudeAndGregorianDate(latitude: Double, longitude: Double, date: String): DayPrayerTimings?

    @Modifying
    @Transactional
    @Query("DELETE FROM DayPrayerTimings c WHERE c.savedIn < :threshold")
    fun deleteOlderThan(threshold: Instant): Int
}
