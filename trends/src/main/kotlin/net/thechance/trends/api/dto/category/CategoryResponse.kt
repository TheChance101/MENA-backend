package net.thechance.trends.api.dto.category

import net.thechance.trends.entity.Category
import java.util.UUID

data class CategoryResponse(
    val id: UUID,
    val name: String,
    val emoji: String
)

fun Category.toCategoryResponse(): CategoryResponse {
    return CategoryResponse(
        id = this.id,
        name = this.name,
        emoji = this.emoji
    )
}