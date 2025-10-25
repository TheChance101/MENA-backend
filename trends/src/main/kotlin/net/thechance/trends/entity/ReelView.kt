package net.thechance.trends.entity

import jakarta.persistence.*
import java.io.Serializable
import java.time.LocalDateTime
import java.util.*

@Table(
    name = "reel_views",
    schema = "trends",
    uniqueConstraints = [UniqueConstraint(columnNames = ["reel_id", "user_id"])]
)
@Entity
@IdClass(ReelViewId::class)
data class ReelView(
    @Id
    @Column(name = "reel_id", nullable = false)
    val reelId: UUID,

    @Id
    @Column(name = "user_id", nullable = false)
    val userId: UUID,

    @Column(name = "viewed_at", nullable = false)
    val viewedAt: LocalDateTime = LocalDateTime.now()
)

data class ReelViewId(
    val reelId: UUID,
    val userId: UUID
) : Serializable