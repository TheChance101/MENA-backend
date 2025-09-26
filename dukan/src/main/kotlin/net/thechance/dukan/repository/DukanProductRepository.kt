package net.thechance.dukan.repository

import net.thechance.dukan.entity.DukanProduct
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface DukanProductRepository : JpaRepository<DukanProduct, UUID> {
    fun findAllByShelfId(shelfId: UUID): List<DukanProduct>

    fun findAllByDukanId(dukanId: UUID): List<DukanProduct>
}
