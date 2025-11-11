package net.thechance.dukan.repository

import net.thechance.dukan.entity.StatusChangelog
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.time.Instant
import java.util.UUID

interface StatusChangelogRepository: JpaRepository<StatusChangelog, UUID> {
    @Modifying
    @Query(
        value = """
            INSERT INTO dukan.status_change_logs (id, dukan_id, status, reason, created_at)
            VALUES (:id, :dukanId, :status, :reason, :createdAt)
        """,
        nativeQuery = true
    )
    fun insertStatusChangelog(
        @Param("id") id: UUID,
        @Param("dukanId") dukanId: UUID,
        @Param("status") status: String,
        @Param("reason") reason: String?,
        @Param("createdAt") createdAt: Instant
    ): Int
}