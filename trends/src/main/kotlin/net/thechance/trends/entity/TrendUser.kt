package net.thechance.trends.entity

import jakarta.persistence.*
import java.util.*
import java.util.Collections.emptySet

@Table(name = "users", schema = "trends")
@Entity
data class TrendUser(
    @Id
    @Column(columnDefinition = "uuid", nullable = false, updatable = false)
    val userId: UUID,

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "user_categories",
        joinColumns = [JoinColumn(name = "user_id")],
        inverseJoinColumns = [JoinColumn(name = "category_id")],
        schema = "trends",
    )
    val categories: MutableSet<Category> = emptySet(),
)
