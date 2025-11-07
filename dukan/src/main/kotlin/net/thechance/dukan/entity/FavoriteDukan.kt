package net.thechance.dukan.entity

import jakarta.persistence.*
import java.io.Serializable
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
    @EmbeddedId
    val id: FavoriteDukanId,

    @Column(name = "created_at", nullable = false)
    val createdAt: Instant = Instant.now(),

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dukan_id", insertable = false, updatable = false, nullable = true)
    val dukan: Dukan? = null
)

@Embeddable
data class FavoriteDukanId(
    @Column(name = "dukan_id", nullable = false)
    val dukanId: UUID = UUID.randomUUID(),
    @Column(name = "user_id", nullable = false)
    val userId: UUID = UUID.randomUUID()
) : Serializable