package net.thechance.dukan.entity

import jakarta.persistence.*
import java.math.BigDecimal
import java.util.*

@Table(
    name = "order_items",
    schema = "dukan"
)
@Entity
class OrderItem(

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID = UUID.randomUUID(),

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    val order: Order,

    @Column(name = "product_id", nullable = false)
    val productId: UUID,

    @Column(name = "product_name", nullable = false)
    val productName: String,

    @Column(name = "product_image", nullable = false)
    val productImage: String,

    @Column(name = "product_price", nullable = false)
    val priceBeforeDiscount: BigDecimal,

    @Column(name = "quantity", nullable = false)
    val quantity: Int,

    @Column(name = "total_price", nullable = false)
    val priceAfterDiscount: BigDecimal
)
