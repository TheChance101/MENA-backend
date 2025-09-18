package net.thechance.trends.entity

import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.*

@Table(name = "reels", schema = "trends")
@Entity
data class Reel(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID = UUID.randomUUID(),
    @Column(name = "owner_id", nullable = false)
    val ownerId: UUID,
    @Column(name = "thumbnail_url", nullable = false)
    val thumbnailUrl: String,
    @Column(name = "video_url", nullable = false)
    val videoUrl: String,
    @Column(name = "description", nullable = false)
    val description: String,
    @Column(name = "likes_count", nullable = false)
    val likesCount: Int = 0,
    @Column(name = "views_count", nullable = false)
    val viewsCount: Int = 0,
    @Column(name = "created_at", nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "reel_categories",
        joinColumns = [JoinColumn(name = "reel_id")],
        inverseJoinColumns = [JoinColumn(name = "category_id")],
        schema = "trends"
    )
    val categories: Set<Category> = emptySet()
)