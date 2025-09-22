package net.thechance.trends.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.JoinTable
import jakarta.persistence.ManyToMany
import jakarta.persistence.Table
import java.util.Collections.emptySet
import java.util.UUID

@Table(name = "user", schema = "trends")
@Entity
data class TrendUser(
    @Id
    @Column(columnDefinition = "uuid", nullable = false, updatable = false)
    val userId: UUID,

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "user_categories",
        joinColumns = [JoinColumn(name = "user_id")],
        inverseJoinColumns = [JoinColumn(name = "category_id")],
        schema = "trends",
    )
    val categories: MutableSet<Category> = emptySet(),
)
