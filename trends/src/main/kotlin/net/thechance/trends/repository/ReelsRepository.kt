package net.thechance.trends.repository

import net.thechance.trends.entity.Reel
import net.thechance.trends.models.ReelWithLikeStatus
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.util.*

interface ReelsRepository : JpaRepository<Reel, UUID> {


    @Query(
        """
        SELECT DISTINCT r AS reel, 
               CASE WHEN rl IS NOT NULL THEN true ELSE false END AS isLiked
        FROM Reel r
        LEFT JOIN ReelLike rl ON rl.reelId = r.id AND rl.userId = :ownerId
        WHERE r.ownerId = :ownerId 
        AND r.isPublished = :isPublished
        """,
        countQuery = """
        SELECT COUNT(r.id)
        FROM Reel r
        WHERE r.ownerId = :ownerId 
        AND r.isPublished = :isPublished
        """
    )
    fun findByOwnerIdAndIsPublished(ownerId: UUID, isPublished: Boolean, pageable: Pageable): Page<ReelWithLikeStatus>

    @Query(
        """
        SELECT DISTINCT r AS reel, 
               CASE WHEN rl IS NOT NULL THEN true ELSE false END AS isLiked
        FROM Reel r
        LEFT JOIN ReelLike rl ON rl.reelId = r.id AND rl.userId = :userId
        WHERE r.id = :reelId 
        AND r.isPublished = :isPublished
        """,
        countQuery = """
        SELECT COUNT(r.id)
        FROM Reel r
        WHERE r.id = :reelId 
        AND r.isPublished = :isPublished
        """
    )
    fun findByIdAndIsPublishedWithLikeStatus(reelId: UUID, userId: UUID, isPublished: Boolean): ReelWithLikeStatus?


    @Query(
        """
        SELECT DISTINCT r AS reel, 
               CASE WHEN rl IS NOT NULL THEN true ELSE false END AS isLiked
        FROM Reel r
        LEFT JOIN ReelLike rl ON rl.reelId = r.id AND rl.userId = :ownerId
        WHERE r.id = :id 
        AND r.ownerId = :ownerId
        """
    )
    fun findByIdAndOwnerId(id: UUID, ownerId: UUID): ReelWithLikeStatus?

    @Query("SELECT r.videoUrl FROM Reel r WHERE r.id = :id AND r.ownerId = :ownerId")
    fun findVideoUrlByIdAndOwnerId(id: UUID, ownerId: UUID): String?

    fun deleteReelById(id: UUID): Int

    @Query(
        """
        SELECT DISTINCT r AS reel, 
               CASE WHEN rl IS NOT NULL THEN true ELSE false END AS isLiked
        FROM Reel r
        JOIN FETCH r.categories rc
        LEFT JOIN ReelLike rl ON rl.reelId = r.id AND rl.userId = :userId
        WHERE r.isPublished = true
        AND rc.id IN (
            SELECT uc.id FROM TrendUser u
            JOIN u.categories uc
            WHERE u.userId = :userId
        )
        AND (:reelId IS NULL OR r.createdAt <= (
            SELECT r2.createdAt FROM Reel r2 WHERE r2.id = :reelId
        ))
        """,
        countQuery = """
        SELECT COUNT(DISTINCT r.id)
        FROM Reel r
        JOIN r.categories rc
        WHERE r.isPublished = true
        AND rc.id IN (
            SELECT uc.id FROM TrendUser u
            JOIN u.categories uc
            WHERE u.userId = :userId
        )
        AND (:reelId IS NULL OR r.createdAt <= (
            SELECT r2.createdAt FROM Reel r2 WHERE r2.id = :reelId
        ))
        """
    )
    fun getReelFeedForUser(
        userId: UUID,
        reelId: UUID?,
        pageable: Pageable
    ): Page<ReelWithLikeStatus>
}