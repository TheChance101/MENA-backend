package net.thechance.dukan.entity

import jakarta.persistence.*
import java.io.Serializable
import java.time.Instant
import java.util.*

@Entity
@Table(
    name = "favorite_dukans",
    schema = "dukan",
    uniqueConstraints = [
        UniqueConstraint(columnNames = ["user_id", "dukan_id"])
    ]
)
@IdClass(FavoriteDukanId::class)
data class FavoriteDukan(
    @Id
    @Column(name = "user_id", nullable = false)
    val userId: UUID,

    @Id
    @Column(name = "dukan_id", nullable = false)
    val dukanId: UUID,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dukan_id", insertable = false, updatable = false, nullable = true)
    val dukan: Dukan? = null,

    @Column(name = "created_at", nullable = false)
    val createdAt: Instant = Instant.now(),
)

data class FavoriteDukanId(
    val userId: UUID = UUID.randomUUID(),
    val dukanId: UUID = UUID.randomUUID()
) : Serializable