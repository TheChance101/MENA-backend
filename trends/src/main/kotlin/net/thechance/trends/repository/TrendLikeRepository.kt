package net.thechance.trends.repository

import net.thechance.trends.entity.TrendLike
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface TrendLikeRepository : JpaRepository<TrendLike, UUID> {
    fun deleteTrendLikeByTrendIdAndUserId(trendId: UUID, userId: UUID): Int
}