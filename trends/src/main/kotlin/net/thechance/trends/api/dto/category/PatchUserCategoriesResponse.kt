package net.thechance.trends.api.dto.category

import net.thechance.trends.api.dto.PatchMetadata

data class PatchUserCategoriesResponse(
    val patchMetadata: PatchMetadata,
    val updatedCategories: List<CategoryResponse>
)
