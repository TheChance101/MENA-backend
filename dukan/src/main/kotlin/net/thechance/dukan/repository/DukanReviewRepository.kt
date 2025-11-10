package net.thechance.dukan.repository

import net.thechance.dukan.entity.DukanReview
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.time.Instant
import java.util.UUID

interface DukanReviewRepository: JpaRepository<DukanReview, UUID> {
    @Modifying
    @Query(
        value = """
            INSERT INTO dukan.dukan_reviews (id, dukan_id, status, rejection_message, created_at)
            VALUES (:id, :dukanId, :status, :rejectionMessage, :createdAt)
        """,
        nativeQuery = true
    )
    fun insertDukanReview(
        @Param("id") id: UUID,
        @Param("dukanId") dukanId: UUID,
        @Param("status") status: String,
        @Param("rejectionMessage") rejectionMessage: String?,
        @Param("createdAt") createdAt: Instant
    ): Int
}