package net.thechance.dukan.repository

import net.thechance.dukan.entity.SoldProduct
import net.thechance.dukan.service.model.DukanProductWithFavoriteAndQuantity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.util.*

interface SoldProductRepository : JpaRepository<SoldProduct, UUID> {

    fun findByProductIdAndDukanId(productId: UUID, dukanId: UUID): SoldProduct?

    @Query(
        """
    SELECT new net.thechance.dukan.service.model.DukanProductWithFavoriteAndQuantity(
        product,
        CASE WHEN isFavorite.id.productId IS NOT NULL THEN true ELSE false END,
        COALESCE(cartItem.quantity, 0)
    )
    FROM SoldProduct soldProduct
    JOIN DukanProduct product
        ON product.id = soldProduct.productId
    LEFT JOIN FavoriteProduct isFavorite
        ON isFavorite.id.productId = product.id AND isFavorite.id.userId = :userId
    LEFT JOIN Cart cart
        ON cart.userId = :userId AND cart.dukanId = product.dukan.id AND cart.isOrderPurchased = false
    LEFT JOIN cart.items cartItem
        ON cartItem.product.id = product.id
    WHERE product.isDeleted = false
      AND product.dukan.id = :dukanId
    GROUP BY product, isFavorite.id.productId, cartItem.quantity
    ORDER BY SUM(soldProduct.quantity) DESC
    """
    )
    fun findTopSellingProducts(
        dukanId: UUID,
        userId: UUID,
        pageable: Pageable
    ): Page<DukanProductWithFavoriteAndQuantity>
}