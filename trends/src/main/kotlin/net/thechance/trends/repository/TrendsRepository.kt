package net.thechance.trends.repository

import net.thechance.trends.entity.Trend
import net.thechance.trends.models.TrendUrls
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
        LEFT JOIN FETCH t.owner
        LEFT JOIN TrendLike tl ON tl.trendId = t.id AND tl.userId = :ownerId
        WHERE t.ownerId = :ownerId 
        AND t.isPublished = :isPublished
        AND (:trendId IS NULL OR t.createdAt <= (
            SELECT t2.createdAt FROM Trend t2 WHERE t2.id = :trendId
        ))
        """,
        countQuery = """
        SELECT COUNT(t.id)
        FROM Trend t
        WHERE t.ownerId = :ownerId 
        AND t.isPublished = :isPublished
        AND (:trendId IS NULL OR t.createdAt <= (
            SELECT t2.createdAt FROM Trend t2 WHERE t2.id = :trendId
        ))
        """
    )
    fun findByOwnerIdAndIsPublished(
        ownerId: UUID,
        isPublished: Boolean,
        trendId: UUID?,
        pageable: Pageable
    ): Page<TrendWithLikeStatus>

    @Query(
        """
        SELECT DISTINCT t AS trend, 
               CASE WHEN tl IS NOT NULL THEN true ELSE false END AS isLiked
        FROM Trend t
        LEFT JOIN FETCH t.owner
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
        LEFT JOIN FETCH t.owner
        LEFT JOIN TrendLike tl ON tl.trendId = t.id AND tl.userId = :ownerId
        WHERE t.id = :id 
        AND t.ownerId = :ownerId
        """
    )
    fun findByIdAndOwnerId(id: UUID, ownerId: UUID): TrendWithLikeStatus?

    @Query("SELECT t.videoUrl AS trendVideoUrl, t.thumbnailUrl AS trendThumbnailUrl FROM Trend t WHERE t.id = :id AND t.ownerId = :ownerId")
    fun findVideoUrlByIdAndOwnerId(id: UUID, ownerId: UUID): TrendUrls?

    @Query("SELECT t.videoUrl AS trendVideoUrl, t.thumbnailUrl AS trendThumbnailUrl FROM Trend t WHERE t.id = :id")
    fun findTrendUrlsById(id: UUID): TrendUrls?

    fun deleteTrendById(id: UUID): Int

    @Query(
        """
        SELECT t.id
        FROM Trend t
        JOIN t.categories tc
        LEFT JOIN UserCategories uc ON uc.categoryId = tc.id AND uc.userId = :userId
        WHERE t.isPublished = true
        AND tc.id IN :categories
        GROUP BY t.id
        HAVING :startTrendId IS NULL 
        OR (COALESCE(MAX(uc.affinity), 0), MAX(t.createdAt)) <= (
           SELECT COALESCE(MAX(uc2.affinity), 0), MAX(t2.createdAt)
           FROM Trend t2
           JOIN t2.categories tc2
           LEFT JOIN UserCategories uc2 ON uc2.categoryId = tc2.id AND uc2.userId = :userId
           WHERE t2.id = :startTrendId
           GROUP BY t2.id
        )
        ORDER BY COALESCE(MAX(uc.affinity), 0) DESC, MAX(t.createdAt) DESC
        """
    )
    fun getTrendIdsOrderedByAffinity(
        userId: UUID,
        categories: List<UUID>,
        startTrendId: UUID? = null,
        pageable: Pageable
    ): Page<UUID>

    @Query(
        """
    SELECT DISTINCT t AS trend, 
           CASE WHEN tl IS NOT NULL THEN true ELSE false END AS isLiked
    FROM Trend t
    LEFT JOIN FETCH t.owner
    LEFT JOIN TrendLike tl ON tl.trendId = t.id AND tl.userId = :userId
    WHERE t.id IN :trendIds
    AND t.isPublished = true
    AND t.owner.status = 'ACTIVE'
    """
    )
    fun getTrendFeedForCategories(
        userId: UUID,
        trendIds: List<UUID>
    ): List<TrendWithLikeStatus>

    @Query(
        """
        SELECT DISTINCT t AS trend, 
               true AS isLiked
        FROM Trend t
        LEFT JOIN FETCH t.owner
        INNER JOIN TrendLike tl ON tl.trendId = t.id AND tl.userId = :userId
        WHERE t.isPublished = true
        AND (:trendId IS NULL OR t.createdAt <= (
            SELECT t2.createdAt FROM Trend t2 WHERE t2.id = :trendId
        ))
        """,
        countQuery = """
        SELECT COUNT(DISTINCT t.id)
        FROM Trend t
        INNER JOIN TrendLike tl ON tl.trendId = t.id AND tl.userId = :userId
        WHERE t.isPublished = true
        AND (:trendId IS NULL OR t.createdAt <= (
            SELECT t2.createdAt FROM Trend t2 WHERE t2.id = :trendId
        ))
        """
    )
    fun getUserLikedTrends(
        userId: UUID,
        trendId: UUID?,
        pageable: Pageable
    ): Page<TrendWithLikeStatus>
}