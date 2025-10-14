package net.thechance.trends.api.dto.category

import net.thechance.trends.entity.Category
import java.util.*

data class CategoryResponse(
    val id: UUID,
    val name: String,
    val emoji: String,
    val isSelected: Boolean
)

fun Category.toCategoryResponse(
    isSelected: Boolean
): CategoryResponse {
    return CategoryResponse(
        id = this.id,
        name = this.name,
        emoji = this.emoji,
        isSelected = isSelected
    )
}