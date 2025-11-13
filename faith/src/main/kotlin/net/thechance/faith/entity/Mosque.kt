package net.thechance.faith.entity

import jakarta.persistence.*
import java.time.Instant
import java.util.UUID

@Entity
@Table(name = "mosque", schema = "faith")
data class Mosque(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID = UUID.randomUUID(),

    @Column(nullable = false)
    val creatorId: UUID,

    @Column(nullable = false)
    val name: String,

    @Column(nullable = false)
    val latitude: Double,

    @Column(nullable = false)
    val longitude: Double,

    @Column(nullable = false)
    val address: String,

    @Column(nullable = false)
    val imageUrl: String,

    @Column(nullable = false)
    val createdAt: Instant = Instant.now()
)
