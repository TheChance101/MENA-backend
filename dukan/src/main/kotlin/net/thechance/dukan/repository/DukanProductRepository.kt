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
    @Query("""
        select p from DukanProduct p
        join fetch p.shelf s
        join fetch s.dukan d
        where s.id = :shelfId
    """)
    fun findAllByShelfIdWithDukan(
        @Param("shelfId") shelfId: UUID,
        pageable: Pageable
    ): Page<DukanProduct>

    @Query("""
        select p from DukanProduct p
        join fetch p.shelf s
        join fetch s.dukan d
        where p.id = :productId
    """)
    fun findByIdWithDukan(
        @Param("productId") productId: UUID
    ): DukanProduct
}
