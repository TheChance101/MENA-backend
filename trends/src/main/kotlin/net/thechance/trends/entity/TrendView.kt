package net.thechance.trends.entity

import jakarta.persistence.*
import java.io.Serializable
import java.time.LocalDateTime
import java.util.*

@Table(
    name = "trend_views",
    schema = "trends",
    uniqueConstraints = [UniqueConstraint(columnNames = ["trend_id", "user_id"])]
)
@Entity
@IdClass(TrendViewId::class)
data class TrendView(
    @Id
    @Column(name = "trend_id", nullable = false)
    val trendId: UUID,

    @Id
    @Column(name = "user_id", nullable = false)
    val userId: UUID,

    @Column(name = "viewed_at", nullable = false)
    val viewedAt: LocalDateTime = LocalDateTime.now()
)

data class TrendViewId(
    val trendId: UUID = UUID.randomUUID(),
    val userId: UUID = UUID.randomUUID()
) : Serializable