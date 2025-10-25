package net.thechance.trends.repository

import net.thechance.trends.entity.ReelLike
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface ReelLikeRepository : JpaRepository<ReelLike, UUID> {
    fun deleteReelLikeByReelIdAndUserId(reelId: UUID, userId: UUID)
}