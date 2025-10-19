package net.thechance.faith.entity

import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import java.time.Instant
import java.time.LocalDate

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

    @Column(nullable = false)
    val date: LocalDate,
    @Column(nullable = false)
    val fajr: Instant,
    @Column(nullable = false)
    val sunrise: Instant,
    @Column(nullable = false)
    val dhuhr: Instant,
    @Column(nullable = false)
    val asr: Instant,
    @Column(nullable = false)
    val maghrib: Instant,
    @Column(nullable = false)
    val isha: Instant
)
