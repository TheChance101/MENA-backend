package net.thechance.dukan.repository

import net.thechance.dukan.entity.DukanProduct
import org.springframework.data.domain.Page
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import org.springframework.data.domain.Pageable
import java.util.*

@Repository
interface DukanProductRepository : JpaRepository<DukanProduct, UUID> {
    fun findAllByShelfId(shelfId: UUID, pageable: Pageable): Page<DukanProduct>
}
