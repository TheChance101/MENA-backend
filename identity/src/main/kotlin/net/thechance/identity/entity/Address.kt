package net.thechance.identity.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.Instant
import java.util.*

@Entity
@Table(name = "Addresses", schema = "identity")
class Address(
    @Id
    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    val id: UUID = UUID.randomUUID(),

    @Column(columnDefinition = "user_id", updatable = false, nullable = false)
    val userId: UUID,

    @Column(columnDefinition = "latitude", nullable = false)
    val latitude: Double,

    @Column(columnDefinition = "longitude", nullable = false)
    val longitude: Double,

    @Column(columnDefinition = "address_line", nullable = false)
    val addressLine: String,

    @Column(columnDefinition = "address_type", nullable = false)
    val addressType: String,

    @Column(columnDefinition = "is_active", nullable = false)
    val isActive: Boolean = false,

    @Column(name = "created_at", nullable = false)
    val createdAt: Instant = Instant.now(),

    @Column(name = "updated_at", nullable = false)
    val updatedAt: Instant = Instant.now(),
)
