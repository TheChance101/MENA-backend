package net.thechance.trends.repository

import net.thechance.trends.entity.ReelLike
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface ReelLikeRepository : JpaRepository<ReelLike, UUID> {
    fun existsByReelIdAndUserId(reelId: UUID, userId: UUID): Boolean
    fun deleteByReelIdAndUserId(reelId: UUID, userId: UUID)
}