package net.thechance.trends.repository

import net.thechance.trends.entity.Trend
import net.thechance.trends.models.TrendWithLikeStatus
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.util.*

interface TrendsRepository : JpaRepository<Trend, UUID> {


    @Query(
        """
        SELECT DISTINCT t AS trend, 
               CASE WHEN tl IS NOT NULL THEN true ELSE false END AS isLiked
        FROM Trend t
        LEFT JOIN TrendLike tl ON tl.trendId = t.id AND tl.userId = :ownerId
        WHERE t.ownerId = :ownerId 
        AND t.isPublished = :isPublished
        """,
        countQuery = """
        SELECT COUNT(t.id)
        FROM Trend t
        WHERE t.ownerId = :ownerId 
        AND t.isPublished = :isPublished
        """
    )
    fun findByOwnerIdAndIsPublished(ownerId: UUID, isPublished: Boolean, pageable: Pageable): Page<TrendWithLikeStatus>

    @Query(
        """
        SELECT DISTINCT t AS trend, 
               CASE WHEN tl IS NOT NULL THEN true ELSE false END AS isLiked
        FROM Trend t
        LEFT JOIN TrendLike tl ON tl.trendId = t.id AND tl.userId = :userId
        WHERE t.id = :trendId 
        AND t.isPublished = :isPublished
        """,
        countQuery = """
        SELECT COUNT(t.id)
        FROM Trend t
        WHERE t.id = :trendId 
        AND t.isPublished = :isPublished
        """
    )
    fun findByIdAndIsPublishedWithLikeStatus(trendId: UUID, userId: UUID, isPublished: Boolean): TrendWithLikeStatus?


    @Query(
        """
        SELECT DISTINCT t AS trend, 
               CASE WHEN tl IS NOT NULL THEN true ELSE false END AS isLiked
        FROM Trend t
        LEFT JOIN TrendLike tl ON tl.trendId = t.id AND tl.userId = :ownerId
        WHERE t.id = :id 
        AND t.ownerId = :ownerId
        """
    )
    fun findByIdAndOwnerId(id: UUID, ownerId: UUID): TrendWithLikeStatus?

    @Query("SELECT t.videoUrl FROM Trend t WHERE t.id = :id AND t.ownerId = :ownerId")
    fun findVideoUrlByIdAndOwnerId(id: UUID, ownerId: UUID): String?

    fun deleteTrendById(id: UUID): Int

    @Query(
        """
        SELECT DISTINCT t AS trend, 
               CASE WHEN tl IS NOT NULL THEN true ELSE false END AS isLiked
        FROM Trend t
        JOIN FETCH t.categories tc
        LEFT JOIN TrendLike tl ON tl.trendId = t.id AND tl.userId = :userId
        WHERE t.isPublished = true
        AND tc.id IN (
            SELECT uc.id FROM TrendUser u
            JOIN u.categories uc
            WHERE u.userId = :userId
        )
        AND (:trendId IS NULL OR t.createdAt <= (
            SELECT t2.createdAt FROM Trend t2 WHERE t2.id = :trendId
        ))
        """,
        countQuery = """
        SELECT COUNT(DISTINCT t.id)
        FROM Trend t
        JOIN t.categories tc
        WHERE t.isPublished = true
        AND tc.id IN (
            SELECT uc.id FROM TrendUser u
            JOIN u.categories uc
            WHERE u.userId = :userId
        )
        AND (:trendId IS NULL OR t.createdAt <= (
            SELECT t2.createdAt FROM Trend t2 WHERE t2.id = :trendId
        ))
        """
    )
    fun getTrendFeedForUser(
        userId: UUID,
        trendId: UUID?,
        pageable: Pageable
    ): Page<TrendWithLikeStatus>
}