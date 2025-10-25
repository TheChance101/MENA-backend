package net.thechance.trends.models

import net.thechance.trends.entity.Reel

interface ReelWithLikeStatus {
    fun getReel(): Reel
    fun getIsLiked(): Boolean
}