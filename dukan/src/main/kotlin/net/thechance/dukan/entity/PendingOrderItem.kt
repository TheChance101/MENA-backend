package net.thechance.dukan.entity

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*
import java.util.*

@Table(name = "pending_order_items", schema = "dukan")
@Entity
data class PendingOrderItem(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID = UUID.randomUUID(),

    @ManyToOne
    @JoinColumn(name = "pending_order_id", nullable = false)
    @JsonIgnore
    val pendingOrder: PendingOrder,

    @Column(name = "product_id", nullable = false)
    val productId: UUID,

    @Column(name = "quantity", nullable = false)
    val quantity: Int
)