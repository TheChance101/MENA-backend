package net.thechance.dukan.entity

import jakarta.persistence.*
import java.math.BigDecimal
import java.time.Instant
import java.util.*

@Table(
    name = "orders",
    schema = "dukan"
)
@Entity
class Order(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID = UUID.randomUUID(),

    @Column(name = "user_id", nullable = false)
    val userId: UUID,

    @Column(name = "dukan_id", nullable = false)
    val dukanId: UUID,

    @Column(name = "transaction_id", nullable = false)
    val transactionId: UUID,

    @Column(name = "total_before_discount", nullable = false)
    val totalBeforeDiscount: BigDecimal,

    @Column(name = "discount_amount", nullable = false)
    val discountPercentage: BigDecimal,

    @Column(name = "total_after_discount", nullable = false)
    val totalAfterDiscount: BigDecimal,

    @Column(name = "platform_fees", nullable = false)
    val platformFees: BigDecimal,

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    var status: OrderStatus = OrderStatus.PENDING,

    @Column(name = " customer_name", nullable = false)
    val customerName: String,

    @Column(name = " customer_phone", nullable = false)
    val customerPhone: String,

    @Column(name = " customer_image", nullable = false)
    val customerImage: String,

    @Column(name = "delivery_address", nullable = false)
    val deliveryAddress: String,

    @Column(name = "delivery_lat", nullable = false)
    val deliveryLat: Double,

    @Column(name = "delivery_lng", nullable = false)
    val deliveryLng: Double,

    @Column(name = "created_at", nullable = false)
    val createdAt: Instant = Instant.now(),

    @Column(name = "finished_at", nullable = true)
    var finishedAt: Instant? = null,

    @OneToMany(mappedBy = "order", cascade = [CascadeType.ALL])
    val items: MutableList<OrderItem> = mutableListOf()
) {
    enum class OrderStatus {
        PENDING,
        SUCCESSFUL,
        FAILED
    }
}