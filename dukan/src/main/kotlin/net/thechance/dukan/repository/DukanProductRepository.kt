package net.thechance.dukan.repository

import net.thechance.dukan.entity.DukanProduct
import net.thechance.dukan.service.model.DukanProductWithFavoriteAndQuantity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface DukanProductRepository : JpaRepository<DukanProduct, UUID> {

    fun existsByDukanIdAndNameIgnoreCase(dukanId: UUID, name: String): Boolean
    fun existsByShelfId(shelfId: UUID): Boolean
    fun findByIdAndDukanOwnerId(id: UUID, ownerId: UUID): Optional<DukanProduct>

    @Query(
        """
        SELECT new net.thechance.dukan.service.model.DukanProductWithFavoriteAndQuantity(
            product,
            CASE WHEN favorite.id.productId IS NOT NULL THEN true ELSE false END,
            COALESCE(cartItem.quantity, 0)
        )
        FROM DukanProduct product
        JOIN FETCH product.shelf shelf
        JOIN FETCH shelf.dukan dukan
        LEFT JOIN FavoriteProduct favorite
            ON favorite.id.productId = product.id AND favorite.id.userId = :userId
        LEFT JOIN Cart cart
            ON cart.userId = :userId AND cart.dukanId = dukan.id
        LEFT JOIN cart.items cartItem
            ON cartItem.product.id = product.id
        WHERE product.id = :productId AND (product.isDeleted = false OR product.isDeleted IS null)
        """
    )
    fun findProductWithFavoriteAndQuantityById(
        @Param("userId") userId: UUID,
        @Param("productId") productId: UUID
    ): DukanProductWithFavoriteAndQuantity

    @Query(
        """
        SELECT new net.thechance.dukan.service.model.DukanProductWithFavoriteAndQuantity(
            product,
            CASE WHEN favorite.id.productId IS NOT NULL THEN true ELSE false END,
            COALESCE(cartItem.quantity, 0)
        )
        FROM DukanProduct product
        JOIN product.shelf shelf
        JOIN shelf.dukan dukan
        LEFT JOIN FavoriteProduct favorite
            ON favorite.id.productId = product.id AND favorite.id.userId = :userId
        LEFT JOIN Cart cart
            ON cart.userId = :userId AND cart.dukanId = dukan.id
        LEFT JOIN cart.items cartItem
            ON cartItem.product.id = product.id
        WHERE shelf.id = :shelfId AND (product.isDeleted = false OR product.isDeleted IS null)
        """
    )
    fun findProductsWithFavoriteAndQuantityByShelf(
        @Param("userId") userId: UUID,
        @Param("shelfId") shelfId: UUID,
        pageable: Pageable
    ): Page<DukanProductWithFavoriteAndQuantity>
}
