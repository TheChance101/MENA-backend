package net.thechance.dukan.repository

import net.thechance.dukan.entity.FavoriteProduct
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface FavoriteProductRepository : JpaRepository<FavoriteProduct, UUID> {

    fun findByUserIdAndProductId(userId: UUID, productId: UUID): FavoriteProduct?
    fun findAllByUserIdAndProductIdIn(userId: UUID, productIds: List<UUID>): List<FavoriteProduct>
}