package net.thechance.trends.api.dto.category

import net.thechance.trends.entity.Category
import net.thechance.trends.models.UserSelectedCategories
import net.thechance.trends.service.TrendUserService
import java.util.*

data class CategoryResponse(
    val id: UUID,
    val name: String,
    val emoji: String,
    val isSelected: Boolean
)

fun UserSelectedCategories.toCategoryResponse(): CategoryResponse {
    return CategoryResponse(
        id = this.id,
        name = this.name,
        emoji = this.emoji,
        isSelected = isSelected
    )
}

fun Category.toUserSelectedCategories(
    isSelected: Boolean
): UserSelectedCategories {
    return UserSelectedCategories(
        id = this.id,
        name = this.name,
        emoji = this.emoji,
        isSelected = isSelected
    )
}