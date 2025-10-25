package net.thechance.trends.models

import net.thechance.trends.entity.Trend

interface TrendWithLikeStatus {
    fun getTrend(): Trend
    fun getIsLiked(): Boolean
}