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

    @Column(name = "dukan_id", nullable = false)
    val dukanId: UUID,

    @Column(name = "created_at", nullable = false)
    val createdAt: Instant = Instant.now(),
)
