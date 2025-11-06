package net.thechance.dukan.repository

import net.thechance.dukan.entity.FavoriteProduct
import net.thechance.dukan.entity.FavoriteProductId
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface FavoriteProductRepository : JpaRepository<FavoriteProduct, FavoriteProductId> {
    fun findAllByIdUserIdAndIdProductIdIn(userId: UUID, productIds: List<UUID>): List<FavoriteProduct>
    fun deleteByIdProductIdAndIdUserId(productId: UUID, userId: UUID): Int
}