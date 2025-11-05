package net.thechance.dukan.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.IdClass
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import java.io.Serializable
import java.time.Instant
import java.util.UUID

@Table(
    name = "favorite_products",
    schema = "dukan",
    uniqueConstraints = [UniqueConstraint(
        columnNames = ["user_id", "product_id"]
    )]
)
@Entity
@IdClass(FavoriteProductId::class)
data class FavoriteProduct(
    @Id
    @Column(name = "product_id", nullable = false)
    val productId: UUID,

    @Id
    @Column(name = "user_id", nullable = false)
    val userId: UUID,

    @Column(name = "favorite_at", nullable = false)
    val favoriteAt: Instant = Instant.now(),

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", insertable = false, updatable = false, nullable = true)
    val product: DukanProduct? = null
)

data class FavoriteProductId(
    val productId: UUID = UUID.randomUUID(),
    val userId: UUID = UUID.randomUUID()
) : Serializable