package net.thechance.dukan.repository

import net.thechance.dukan.entity.FavoriteDukan
import net.thechance.dukan.entity.FavoriteDukanId
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface FavoriteDukanRepository : JpaRepository<FavoriteDukan, FavoriteDukanId> {
    fun findByIdUserIdAndIdDukanId(userId: UUID, dukanId: UUID): FavoriteDukan?
    fun deleteByIdUserIdAndIdDukanId(userId: UUID, dukanId: UUID): Int
    fun findByIdUserIdAndIdDukanIdIn(userId: UUID, dukanIds: List<UUID>): List<FavoriteDukan>
}