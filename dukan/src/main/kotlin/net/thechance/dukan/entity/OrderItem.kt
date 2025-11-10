package net.thechance.dukan.entity

import jakarta.persistence.*
import java.util.*

@Table(name = "order_items", schema = "dukan")
@Entity
data class OrderItem(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID = UUID.randomUUID(),

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    val order: Order,

    @Column(name = "product_id", nullable = false)
    val productId: UUID,

    @Column(name = "quantity", nullable = false)
    val quantity: Int,

    @Column(name = "unit_price", nullable = false)
    val unitPrice: Double
)