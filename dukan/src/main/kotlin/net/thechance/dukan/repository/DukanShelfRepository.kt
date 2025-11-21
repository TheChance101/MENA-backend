package net.thechance.dukan.repository

import net.thechance.dukan.entity.DukanShelf
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface DukanShelfRepository : JpaRepository<DukanShelf, UUID> {
    fun existsByTitleAndDukanId(title: String, dukanId: UUID): Boolean
    fun findAllByDukanId(dukanId: UUID): List<DukanShelf>
    fun findByIdAndDukanId(id: UUID, dukanId: UUID): DukanShelf?

    @Query(
        """
    SELECT dukanShelf FROM DukanShelf dukanShelf 
    JOIN DukanProduct dukanProduct ON dukanProduct.shelf.id = dukanShelf.id 
    WHERE dukanShelf.dukan.id = :dukanId 
    AND dukanProduct.isDeleted = false
    GROUP BY dukanShelf
"""
    )
    fun findAllByDukanId(
        dukanId: UUID,
        pageable: Pageable
    ): Page<DukanShelf>
}