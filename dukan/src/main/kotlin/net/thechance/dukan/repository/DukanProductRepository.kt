package net.thechance.dukan.repository

import net.thechance.dukan.entity.DukanProduct
import org.springframework.data.domain.Page
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.*

@Repository
interface DukanProductRepository : JpaRepository<DukanProduct, UUID> {
    fun existsByDukanIdAndNameIgnoreCase(dukanId: UUID, name: String): Boolean
    fun existsByShelfId(shelfId: UUID): Boolean
    fun findByIdAndDukanOwnerId(id: UUID, ownerId: UUID): Optional<DukanProduct>

    @Query(
        """
        SELECT product 
        FROM DukanProduct product
        JOIN FETCH product.shelf shelf
        JOIN FETCH shelf.dukan dukan
        WHERE shelf.id = :shelfId
        """
    )
    fun findAllByShelfIdWithDukan(
        @Param("shelfId") shelfId: UUID,
        pageable: Pageable
    ): Page<DukanProduct>

    @Query(
        """
        SELECT product 
        FROM DukanProduct product
        JOIN FETCH product.shelf shelf
        JOIN FETCH shelf.dukan dukan
        WHERE product.id = :productId
        """
    )
    fun findByIdWithDukan(
        @Param("productId") productId: UUID
    ): DukanProduct
}
