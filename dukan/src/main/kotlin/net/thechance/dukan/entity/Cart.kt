package net.thechance.dukan.entity

import jakarta.persistence.*
import java.math.BigDecimal
import java.math.RoundingMode
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

    @Embedded
    var price: Price = Price(BigDecimal.ZERO, BigDecimal.ZERO),

    @Column(name = "is_order_purchased", nullable = false)
    var isOrderPurchased: Boolean = false,

    @Column(name = "created_at", nullable = false)
    val createdAt: Instant = Instant.now(),

    @Column(name = "updated_at", nullable = false)
    var updatedAt: Instant = Instant.now()
) {
    fun calculateTotalPrice() {
        val safeItems = items ?: mutableSetOf()
        if (safeItems.isEmpty()) return

        val totalBefore = safeItems.sumOf {
            it.product.price.base.multiply(BigDecimal(it.quantity))
        }

        val totalAfter = safeItems.sumOf {
            val basePrice = it.product.price.base
            val discount = it.product.discount
            val discountedPrice = basePrice.subtract(
                basePrice.multiply(discount).divide(BigDecimal(100), 2, RoundingMode.HALF_UP)
            )
            discountedPrice.multiply(BigDecimal(it.quantity))
        }

        price = price.copy(
            base = totalBefore,
            final = totalAfter
        )

        updatedAt = Instant.now()
    }
    fun getDiscountPercentage(): BigDecimal {
        return if (price.base > BigDecimal.ZERO) {
            price.base.subtract(price.final)
                .divide(price.base, 10, RoundingMode.HALF_UP)
                .multiply(BigDecimal(100))
                .setScale(2, RoundingMode.HALF_UP)
        } else BigDecimal.ZERO
    }
    @PrePersist
    @PreUpdate
    fun beforeSave() = calculateTotalPrice()
}