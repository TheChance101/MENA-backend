package net.thechance.trends.api.dto.category

import java.util.UUID

data class SubmitUserCategoriesResponse(
    val userId: UUID,
    val categoryIds: List<UUID>,
    val message: String
)
