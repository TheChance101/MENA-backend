package net.thechance.faith.entity

import jakarta.persistence.*
import java.time.LocalDate
import java.util.UUID

@Entity
@Table(name = "mosque", schema = "faith")
data class Mosque(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID = UUID.randomUUID(),
    @Column
    val name: String,
    @Column
    val latitude: Double,
    @Column
    val longitude: Double,
    @Column
    val address: String,
    @Column
    val createdAt: LocalDate,

    @ElementCollection
    @Column(name = "image_url", nullable = false)
    val imageUrls: List<String>? = null,
)
