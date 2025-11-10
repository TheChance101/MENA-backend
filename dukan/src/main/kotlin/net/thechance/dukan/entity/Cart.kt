package net.thechance.dukan.entity

import jakarta.persistence.*
import java.time.Instant
import java.util.*

@Table(
    name = "carts",
    schema = "dukan"
)
@Entity
data class Cart(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID = UUID.randomUUID(),

    @Column(name = "user_id", nullable = false)
    val userId: UUID,

    @Column(name = "dukan_id", nullable = false)
    val dukanId: UUID,

    @OneToMany(mappedBy = "cart", cascade = [CascadeType.ALL], orphanRemoval = true)
    val items: MutableSet<CartItem> = mutableSetOf(),

    @Column(name = "total_price", nullable = false)
    var totalPrice: Double = 0.0,

    @Column(name = "is_order_purchased", nullable = false)
    var isOrderPurchased: Boolean = false,

    @Column(name = "created_at", nullable = false)
    val createdAt: Instant = Instant.now(),

    @Column(name = "updated_at", nullable = false)
    var updatedAt: Instant = Instant.now()
) {
    fun calculateTotalPrice() {
        totalPrice = (items ?: emptySet()).sumOf { it.quantity * it.product.price }
        updatedAt = Instant.now()
    }

    @PrePersist
    @PreUpdate
    fun beforeSave() = calculateTotalPrice()
}