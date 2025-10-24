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
    @Column(name = "uuid", updatable = false, nullable = false)
    val id: UUID = UUID.randomUUID(),

    @Column(name = "user_id", nullable = false)
    val userId: UUID,

    @Column(name = "latitude", nullable = false)
    val latitude: Double,

    @Column(name = "longitude", nullable = false)
    val longitude: Double,

    @Column(name = "address_line", nullable = false)
    val addressLine: String,

    @Column(name = "address_type", nullable = false)
    val addressType: String,

    @Column(name = "is_active", nullable = false)
    val isActive: Boolean = false,

    @Column(name = "created_at", nullable = false)
    val createdAt: Instant = Instant.now(),

    @Column(name = "updated_at", nullable = false)
    val updatedAt: Instant = Instant.now(),
)

fun Address.copy(
    id: UUID = this.id,
    userId: UUID = this.userId,
    latitude: Double = this.latitude,
    longitude: Double = this.longitude,
    addressLine: String = this.addressLine,
    addressType: String = this.addressType,
    isActive: Boolean = this.isActive,
    createdAt: Instant = this.createdAt,
    updatedAt: Instant = this.updatedAt,
): Address {
    return Address(
        id = id,
        userId = userId,
        latitude = latitude,
        longitude = longitude,
        addressLine = addressLine,
        addressType = addressType,
        isActive = isActive,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}