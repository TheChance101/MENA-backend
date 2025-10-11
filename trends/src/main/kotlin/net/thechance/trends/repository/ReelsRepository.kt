package net.thechance.trends.repository

import net.thechance.trends.entity.Reel
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.util.*

interface ReelsRepository : JpaRepository<Reel, UUID> {
    fun findByOwnerIdAndIsPublished(ownerId: UUID, isPublished: Boolean, pageable: Pageable): Page<Reel>
    fun findByIdAndOwnerId(id: UUID, ownerId: UUID): Reel?
    fun existsByIdAndOwnerId(id: UUID, ownerId: UUID): Boolean

    @Query(
        """
    SELECT DISTINCT r FROM Reel r
    JOIN FETCH r.categories rc
    WHERE r.isPublished = true
    AND rc.id IN (
        SELECT uc.id FROM TrendUser u
        JOIN u.categories uc
        WHERE u.userId = :userId
        )
    AND (:reelId IS NULL OR r.createdAt <= (
        SELECT r2.createdAt FROM Reel r2 WHERE r2.id = :reelId
        )
    )
    """
    )
    fun getReelFeedForUser(
        userId: UUID,
        reelId: UUID?,
        pageable: Pageable
    ): Page<Reel>
}