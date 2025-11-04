package net.thechance.dukan.entity

import jakarta.persistence.*
import java.time.Instant
import java.util.*

@Table(
    name = "favorite_dukans",
    schema = "dukan",
    uniqueConstraints = [UniqueConstraint(
        columnNames = ["user_id", "dukan_id"]
    )]
)
@Entity
data class FavoriteDukan(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID = UUID.randomUUID(),

    @Column(name = "user_id", nullable = false)
    val userId: UUID,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dukan_id", nullable = false)
    val dukan: Dukan,

    @Column(name = "created_at", nullable = false)
    val createdAt: Instant = Instant.now(),

    @Column(name = "updated_at", nullable = false)
    val updatedAt: Instant = Instant.now()
)
