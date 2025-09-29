package net.thechance.faith.entity

import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import java.time.Instant
import java.util.*

@Entity
@Table(name = "ayah_bookmark", schema = "faith")
data class AyahBookmark(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false, nullable = false)
    val id: Int = 0,

    @Column(nullable = false)
    val userId: UUID,

    @Column(nullable = false)
    val surahId: Int,

    @Column(nullable = false)
    val ayahNumber: Int,

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    val createdAt: Instant = Instant.now(),
)