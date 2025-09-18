package net.thechance.trends.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.*

@Table(name = "categories", schema = "trends")
@Entity
data class Category(
    @Id
    @Column(columnDefinition = "uuid", nullable = false, updatable = false)
    val id: UUID = UUID.randomUUID(),

    @Column(name = "name", nullable = false, unique = true)
    val name: String,

    @Column(name = "emoji", nullable = false, unique = false)
    val emoji: String,
)
