package net.thechance.trends.repository

import net.thechance.trends.entity.Category
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
    """
    )
    fun getReelFeedForUser(
        userId: UUID,
        pageable: Pageable
    ): Page<Reel>

    @Query(
        """
    SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END
    FROM Reel r
    JOIN r.categories rc
    WHERE r.id = :reelId
    AND r.isPublished = true
    AND rc.id IN (
        SELECT uc.id FROM TrendUser u
        JOIN u.categories uc
        WHERE u.userId = :userId
        )
    """
    )
    fun isReelInUserFeed(userId: UUID, reelId: UUID): Boolean
}