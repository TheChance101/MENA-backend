package net.thechance.trends.entity

import jakarta.persistence.*
import java.util.*

@Table(name = "trends")
@Entity
data class Category(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID = UUID.randomUUID(),
    @Column(
        name = "name",
        nullable = false,
        unique = true
    )
    val name: String,
    @Column(
        name = "emoji",
        nullable = false,
        unique = false
    )
    val emoji: String,
)
