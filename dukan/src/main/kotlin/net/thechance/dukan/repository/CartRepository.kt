package net.thechance.dukan.repository

import net.thechance.dukan.entity.Cart
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.util.*

interface CartRepository : JpaRepository<Cart, UUID> {
    @Query(
        """
        select c from Cart c
        join fetch c.items i
        join fetch i.product p
        where c.userId = :userId and c.dukanId = :dukanId
    """
    )
    fun findByUserIdAndDukanIdWithItemsAndProducts(userId: UUID, dukanId: UUID): Cart?
}