package net.thechance.trends.repository

import net.thechance.trends.entity.Category
import net.thechance.trends.entity.Reel
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.util.*

interface ReelsRepository : JpaRepository<Reel, UUID> {
    fun findByOwnerId(ownerId: UUID, pageable: Pageable): Page<Reel>
    fun findByIdAndOwnerId(id: UUID, ownerId: UUID): Reel?
    fun existsByIdAndOwnerId(id: UUID, ownerId: UUID): Boolean

    @Query("""
    SELECT DISTINCT r FROM Reel r
    JOIN FETCH r.categories rc
    WHERE r.isPublished = true
    AND rc.id IN (
        SELECT uc.id FROM TrendUser u
        JOIN u.categories uc
        WHERE u.userId = :userId
        )
    """)
    fun getReelFeedForUser(
        userId: UUID,
        pageable: Pageable
    ): Page<Reel>
}