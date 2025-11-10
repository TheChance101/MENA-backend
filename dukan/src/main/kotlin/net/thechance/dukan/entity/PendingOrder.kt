package net.thechance.dukan.entity

import jakarta.persistence.*
import java.time.Instant
import java.util.*

@Table(name = "pending_orders", schema = "dukan")
@Entity
data class PendingOrder(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID = UUID.randomUUID(),

    @Column(name = "transaction_id", nullable = false, unique = true)
    val transactionId: UUID,

    @Column(name = "user_id", nullable = false)
    val userId: UUID,

    @Column(name = "dukan_id", nullable = false)
    val dukanId: UUID,

    @Column(name = "address", nullable = false)
    val address: String,

    @Column(name = "longitude", nullable = false)
    val longitude: Double,

    @Column(name = "latitude", nullable = false)
    val latitude: Double,

    @Column(name = "total_price", nullable = false)
    val totalPrice: Double,

    @Column(name = "created_at", nullable = false)
    val createdAt: Instant = Instant.now(),

    @OneToMany(mappedBy = "pendingOrder", cascade = [CascadeType.ALL], orphanRemoval = true)
    val items: MutableSet<PendingOrderItem> = mutableSetOf()
)