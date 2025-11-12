package net.thechance.dukan.entity

import jakarta.persistence.*
import java.math.BigDecimal
import java.time.Instant
import java.util.*

@Table(
    name = "carts",
    schema = "dukan",
    uniqueConstraints = [
        UniqueConstraint(columnNames = ["user_id", "dukan_id"])
    ]
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

    @Column(name = "total_price", nullable = false, precision = 10, scale = 2)
    var totalPrice: BigDecimal = BigDecimal.ZERO,


    @Column(name = "created_at", nullable = false)
    val createdAt: Instant = Instant.now(),

    @Column(name = "updated_at", nullable = false)
    var updatedAt: Instant = Instant.now()
) {
    fun calculateTotalPrice() {
        totalPrice = items.sumOf {
            it.product.price.final.multiply(BigDecimal(it.quantity))
        }
        updatedAt = Instant.now()
    }

    @PrePersist
    @PreUpdate
    fun beforeSave() = calculateTotalPrice()
}