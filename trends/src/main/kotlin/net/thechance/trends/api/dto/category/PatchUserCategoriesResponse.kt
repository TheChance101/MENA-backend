package net.thechance.trends.api.dto.category

data class PatchUserCategoriesResponse(
    val added: List<CategoryResponse>,
    val removed: List<CategoryResponse>,
    val current: List<CategoryResponse>
)
