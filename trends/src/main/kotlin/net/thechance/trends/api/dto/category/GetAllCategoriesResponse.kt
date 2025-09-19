package net.thechance.trends.api.dto.category

data class GetAllCategoriesResponse(
    val message: String,
    val data: List<CategoryResponse>
)
