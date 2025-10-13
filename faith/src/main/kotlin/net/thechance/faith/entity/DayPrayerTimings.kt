package net.thechance.faith.entity

import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import java.time.Instant

@Entity
@Table(name = "day_prayer_timings", schema = "faith")
data class DayPrayerTimings(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false, nullable = false)
    val id: Int,
    @Column(nullable = false)
    val latitude: Double,
    @Column(nullable = false)
    val longitude: Double,
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    val savedIn: Instant = Instant.now(),
    // Gregorian Date Info
    @Column(nullable = false)
    val gregorianDate: String,
    @Column(nullable = false)
    val dateTimestamp: String,
    @Column(nullable = false)
    val gregorianReadableDate: String,
    @Column(nullable = false)
    val gregorianDay: String,
    @Column(nullable = false)
    val gregorianDayName: String,
    @Column(nullable = false)
    val gregorianMonth: Int,
    @Column(nullable = false)
    val gregorianMonthName: String,
    @Column(nullable = false)
    val gregorianYear: String,
    // Hijri Date Info
    @Column(nullable = false)
    val hijriDate: String,
    @Column(nullable = false)
    val hijriReadableDate: String,
    @Column(nullable = false)
    val hijriDay: String,
    @Column(nullable = false)
    val hijriDayName: String,
    @Column(nullable = false)
    val hijriDayArabicName: String,
    @Column(nullable = false)
    val hijriMonth: Int,
    @Column(nullable = false)
    val hijriYear: String,
    @Column(nullable = false)
    val hijriMonthName: String,
    @Column(nullable = false)
    val hijriMonthArabicName: String,
    // Prayer Timings
    @Column(nullable = false)
    val fajr: String,
    @Column(nullable = false)
    val sunrise: String,
    @Column(nullable = false)
    val dhuhr: String,
    @Column(nullable = false)
    val asr: String,
    @Column(nullable = false)
    val maghrib: String,
    @Column(nullable = false)
    val isha: String
)
