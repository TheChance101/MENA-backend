package net.thechance.dukan.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.time.Instant
import java.util.UUID

@Table(name = "sold_products",schema = "dukan")
@Entity
data class SoldProduct(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID = UUID.randomUUID(),

    @JoinColumn(name = "product_id", nullable = false)
    val productId: UUID,

    @JoinColumn(name = "dukan_id", nullable = false)
    val dukanId: UUID,

    @Column(name = "quantity", nullable = false)
    val quantity: Int,

    @Column(name = "sold_at", nullable = false)
    val soldAt: Instant = Instant.now()
)