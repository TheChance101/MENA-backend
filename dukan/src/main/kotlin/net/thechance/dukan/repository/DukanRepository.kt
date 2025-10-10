package net.thechance.dukan.repository

import net.thechance.dukan.entity.Dukan
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface DukanRepository : JpaRepository<Dukan, UUID> {
    fun existsByName(name: String): Boolean
    fun existsByOwnerId(ownerId: UUID): Boolean
    fun findByOwnerId(ownerId: UUID): Dukan
    fun findAllByCategoriesId(categoryId: UUID, pageable: Pageable): Page<Dukan>
    @Query(
        """
    SELECT DISTINCT d
    FROM Dukan d
    WHERE d.status = net.thechance.dukan.entity.Dukan.Status.APPROVED
    AND EXISTS (
        SELECT 1 
        FROM DukanShelf s
        WHERE s.dukan = d
    )
    AND EXISTS (
        SELECT 1
        FROM DukanProduct p
        WHERE p.dukan = d
    )
    ORDER BY d.createdAt DESC
    """
    )
    fun findAllApprovedWithShelvesAndProducts(pageable: Pageable): Page<Dukan>


}