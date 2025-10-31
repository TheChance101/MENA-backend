package net.thechance.trends.entity

import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.Formula
import java.time.LocalDateTime
import java.util.*
import java.util.Collections.emptySet

@Table(name = "trends", schema = "trends")
@Entity
data class Trend(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID = UUID.randomUUID(),

    @Column(name = "owner_id", nullable = false)
    val ownerId: UUID,
    @Column(name = "thumbnail_url", nullable = true, columnDefinition = "TEXT")
    val thumbnailUrl: String? = null,
    @Column(name = "video_url", nullable = false, columnDefinition = "TEXT")
    val videoUrl: String,
    @Column(name = "description", nullable = false, columnDefinition = "TEXT")
    val description: String = "",

    @Formula("(SELECT COUNT(*) FROM trends.trend_likes rl WHERE rl.trend_id = id)")
    val likesCount: Int = 0,

    @Formula("(SELECT COUNT(*) FROM trends.trend_views rv WHERE rv.trend_id = id)")
    val viewsCount: Int = 0,

    @Column(name = "created_at", nullable = false)
    @CreationTimestamp
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "trend_categories",
        joinColumns = [JoinColumn(name = "trend_id")],
        inverseJoinColumns = [JoinColumn(name = "category_id")],
        schema = "trends"
    )
    val categories: MutableSet<Category> = emptySet(),

    @Column(name = "is_published")
    val isPublished: Boolean = false,

    @OneToMany(mappedBy = "trendId", fetch = FetchType.LAZY)
    val likes: MutableSet<TrendLike> = emptySet(),
)