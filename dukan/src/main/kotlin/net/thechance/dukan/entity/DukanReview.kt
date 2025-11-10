package net.thechance.dukan.entity

import jakarta.persistence.*
import java.time.Instant
import java.util.*

@Entity
@Table(name = "dukan_reviews")
data class DukanReview(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID = UUID.randomUUID(),

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dukan_id", nullable = false)
    val dukan: Dukan,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val status: Dukan.Status,

    @Column(nullable = true)
    val rejectionMessage: String? = null,

    @Column(nullable = false)
    val createdAt: Instant = Instant.now()
)
