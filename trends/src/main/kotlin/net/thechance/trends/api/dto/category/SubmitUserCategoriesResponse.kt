package net.thechance.trends.api.dto.category

import java.util.UUID

data class SubmitUserCategoriesResponse(
    val categoryIds: List<UUID>,
)
