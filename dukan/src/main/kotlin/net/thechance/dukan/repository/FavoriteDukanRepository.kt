package net.thechance.dukan.repository

import net.thechance.dukan.entity.FavoriteDukan
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface FavoriteDukanRepository : JpaRepository<FavoriteDukan, UUID> {
    fun findByUserIdAndDukanId(userId: UUID, dukanId: UUID): FavoriteDukan?
    fun findByUserIdAndDukanIdIn(userId: UUID,dukanIds:List<UUID>):List<FavoriteDukan>
}