package net.thechance.faith.entity

import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import java.time.Instant
import java.util.*

@Entity
@Table(name = "bookmarks", schema = "faith")
data class Bookmark(
    @Id
    @GeneratedValue
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