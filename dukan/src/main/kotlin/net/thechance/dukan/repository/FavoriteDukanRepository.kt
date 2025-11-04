package net.thechance.dukan.repository

import net.thechance.dukan.entity.FavoriteDukan
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface FavoriteDukanRepository : JpaRepository<FavoriteDukan, UUID> {

    fun existsByUserIdAndDukanId(userId: UUID, dukanId: UUID): Boolean

    fun deleteByUserIdAndDukanId(userId: UUID, dukanId: UUID)

    @Query(
        """
        SELECT FavoriteDukan
        FROM FavoriteDukan FavoriteDukan
        JOIN FETCH FavoriteDukan.dukan d
        WHERE FavoriteDukan.userId = :userId
        """
    )
    fun findAllWithDukanByUserId(userId: UUID): List<FavoriteDukan>
}
