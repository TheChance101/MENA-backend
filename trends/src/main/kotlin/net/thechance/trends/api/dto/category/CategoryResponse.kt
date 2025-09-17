package net.thechance.trends.api.dto.category

import java.util.UUID

data class CategoryResponse(
    val id: UUID,
    val name: String,
    val emoji: String
)