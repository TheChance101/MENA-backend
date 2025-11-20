package net.thechance.dukan.repository

import net.thechance.dukan.entity.Cart
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.util.*

interface CartRepository : JpaRepository<Cart, UUID> {
    @Query(
        """
        SELECT cart 
        FROM Cart cart
        JOIN FETCH cart.items item
        JOIN FETCH item.product product
        WHERE (cart.isOrderPurchased = false OR cart.isOrderPurchased IS NOT NULL)
        AND cart.userId = :userId 
        AND cart.dukanId = :dukanId
        AND product.isDeleted = false
        """
    )
    fun findByUserIdAndDukanIdAndIsOrderPurchasedFalse(userId: UUID, dukanId: UUID): Cart?
}