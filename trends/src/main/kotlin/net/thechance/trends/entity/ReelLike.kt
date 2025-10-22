package net.thechance.trends.entity

import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.*

@Table(name = "reel_likes", schema = "trends")
@Entity
data class ReelLike(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID = UUID.randomUUID(),

    @Column(name = "reel_id", nullable = false)
    val reelId: UUID,

    @Column(name = "user_id", nullable = false)
    val userId: UUID,

    @Column(name = "liked_at", nullable = false)
    val likedAt: LocalDateTime = LocalDateTime.now()
)