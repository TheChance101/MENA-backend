package net.thechance.dukan.entity

import jakarta.persistence.*
import java.time.Instant
import java.util.*

@Table(name = "orders", schema = "dukan")
@Entity
data class Order(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID = UUID.randomUUID(),

    @Column(name = "user_id", nullable = false)
    val userId: UUID,

    @Column(name = "dukan_id", nullable = false)
    val dukanId: UUID,

    @Column(name = "total_price", nullable = false)
    val totalPrice: Double,

    @Column(name = "total_products", nullable = false)
    val totalProducts: Int,

    @Column(name = "address", nullable = false)
    val address: String,

    @Column(name = "longitude", nullable = false)
    val longitude: Double,

    @Column(name = "latitude", nullable = false)
    val latitude: Double,

    @OneToMany(mappedBy = "order", cascade = [CascadeType.ALL], orphanRemoval = true)
    val items: MutableSet<OrderItem> = mutableSetOf(),

    @Column(name = "created_at", nullable = false)
    val createdAt: Instant = Instant.now()
)