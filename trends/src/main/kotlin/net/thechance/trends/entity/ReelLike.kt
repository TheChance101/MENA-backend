package net.thechance.trends.entity

import jakarta.persistence.*
import java.io.Serializable
import java.time.LocalDateTime
import java.util.*

@Table(
    name = "reel_likes",
    schema = "trends",
    uniqueConstraints = [UniqueConstraint(columnNames = ["reel_id", "user_id"])]
)
@Entity
@IdClass(ReelLikeId::class)
data class ReelLike(

    @Id
    @Column(name = "reel_id", nullable = false)
    val reelId: UUID,

    @Id
    @Column(name = "user_id", nullable = false)
    val userId: UUID,

    @Column(name = "liked_at", nullable = false)
    val likedAt: LocalDateTime = LocalDateTime.now()
)

data class ReelLikeId(
    val reelId: UUID,
    val userId: UUID
) : Serializable