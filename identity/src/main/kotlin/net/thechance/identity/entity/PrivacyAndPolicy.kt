package net.thechance.identity.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.hibernate.annotations.ColumnDefault
import java.time.Instant
import java.util.UUID

@Entity
@Table(name = "privacy_and_policy", schema = "identity")
class PrivacyAndPolicy(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @ColumnDefault("gen_random_uuid()")
    val id: UUID = UUID.randomUUID(),

    @Column(name = "title", nullable = false)
    val title: String,

    @Column(name = "content", nullable = false, length = 512)
    val content: String,

    @Column(name = "language", nullable = false)
    val language: String,

    @Column(name = "show_order", nullable = false)
    val showOrder: UInt,

    @Column(name = "created_at", nullable = false)
    val createdAt: Instant = Instant.now(),

    @Column(name = "updated_at", nullable = false)
    val updatedAt: Instant = Instant.now(),
)
