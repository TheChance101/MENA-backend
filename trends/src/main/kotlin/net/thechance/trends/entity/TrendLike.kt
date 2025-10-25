package net.thechance.trends.entity

import jakarta.persistence.*
import java.io.Serializable
import java.time.LocalDateTime
import java.util.*

@Table(
    name = "trend_likes",
    schema = "trends",
    uniqueConstraints = [UniqueConstraint(columnNames = ["trend_id", "user_id"])]
)
@Entity
@IdClass(TrendLikeId::class)
data class TrendLike(

    @Id
    @Column(name = "trend_id", nullable = false)
    val trendId: UUID,

    @Id
    @Column(name = "user_id", nullable = false)
    val userId: UUID,

    @Column(name = "liked_at", nullable = false)
    val likedAt: LocalDateTime = LocalDateTime.now()
)

data class TrendLikeId(
    val trendId: UUID = UUID.randomUUID(),
    val userId: UUID = UUID.randomUUID()
) : Serializable