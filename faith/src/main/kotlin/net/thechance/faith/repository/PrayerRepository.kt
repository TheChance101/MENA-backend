package net.thechance.faith.repository

import jakarta.transaction.Transactional
import net.thechance.faith.entity.DayPrayerTimings
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.time.Instant
import java.time.LocalDate

interface PrayerRepository : JpaRepository<DayPrayerTimings, Int> {

    @Query(
        """
    SELECT d FROM DayPrayerTimings d
    WHERE d.latitude BETWEEN :#{#latitude - #degreeRange} AND :#{#latitude + #degreeRange}
    AND d.longitude BETWEEN :#{#longitude - #degreeRange} AND :#{#longitude + #degreeRange}
    AND d.date = :date
    ORDER BY 
        ABS(d.latitude - :latitude) + ABS(d.longitude - :longitude)
    """
    )
    fun findByLatitudeAndLongitudeAndDateSortedByNearestLocation(
        @Param("latitude") latitude: Double,
        @Param("longitude") longitude: Double,
        @Param("date") date: LocalDate,
        @Param("degreeRange") degreeRange: Double = 0.1
    ): List<DayPrayerTimings>?

    @Modifying
    @Transactional
    @Query("DELETE FROM DayPrayerTimings c WHERE c.savedIn < :threshold")
    fun deleteOlderThan(threshold: Instant): Int
}
