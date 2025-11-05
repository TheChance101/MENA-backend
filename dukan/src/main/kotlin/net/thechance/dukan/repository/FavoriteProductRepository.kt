package net.thechance.dukan.repository

import net.thechance.dukan.entity.FavoriteProduct
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface FavoriteProductRepository : JpaRepository<FavoriteProduct, UUID> {

    fun deleteFavoriteProductByProductIdAndUserId(productId: UUID, userId: UUID): Int
}