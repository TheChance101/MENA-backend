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

    @Column(name = "platform_fees", nullable = false, precision = 18, scale = 2)
    var platformFees: BigDecimal = BigDecimal.ZERO,

    @Column(name = "created_at", nullable = false)
    val createdAt: Instant = Instant.now(),

    @Column(name = "updated_at", nullable = false)
    var updatedAt: Instant = Instant.now()
) {
    fun calculateTotalPrice() {
        val safeItems = items ?: mutableSetOf()
        if (safeItems.isEmpty()) {
            price = Price(BigDecimal.ZERO, BigDecimal.ZERO)
            return
        }

        val totalBefore = safeItems.sumOf {
            val basePrice = it.product.price.base ?: BigDecimal.ZERO
            basePrice.multiply(BigDecimal(it.quantity))
        }

        val totalAfter = safeItems.sumOf {
            val basePrice = it.product.price.base ?: BigDecimal.ZERO
            val discount = it.product.discount ?: BigDecimal.ZERO
            val discountedPrice = basePrice.subtract(
                basePrice.multiply(discount).divide(BigDecimal(100), 2, RoundingMode.HALF_UP)
            )
            discountedPrice.multiply(BigDecimal(it.quantity))
        }

        price = Price(
            base = totalBefore,
            final = totalAfter
        )

        updatedAt = Instant.now()
    }

    fun applyPlatformFees() {
        val feePercentage = BigDecimal("0.02") // 2%

        val fee = price.final.multiply(feePercentage)
            .setScale(2, RoundingMode.HALF_UP)

        platformFees = fee

        price = Price(
            base = price.base.add(fee),
            final = price.final.add(fee)
        )
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
    fun beforeSave() {
        calculateTotalPrice()
        applyPlatformFees()
    }
}