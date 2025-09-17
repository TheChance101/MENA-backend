package net.thechance.trends.api.dto.category

import net.thechance.trends.entity.Category

fun Category.toCategoryResponse(): CategoryResponse {
    return CategoryResponse(
        id = this.id,
        name = this.name,
        emoji = this.emoji
    )
}