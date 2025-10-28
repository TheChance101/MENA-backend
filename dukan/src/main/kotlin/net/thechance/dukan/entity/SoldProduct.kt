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

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    val product: DukanProduct,

    @ManyToOne
    @JoinColumn(name = "dukan_id", nullable = false)
    val dukan: Dukan,

    @Column(name = "quantity", nullable = false)
    val quantity: Int,

    @Column(name = "sold_at", nullable = false)
    val soldAt: Instant = Instant.now()
){
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as SoldProduct
        return id == other.id
    }

    override fun hashCode(): Int = id.hashCode()
}
