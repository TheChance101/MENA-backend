package net.thechance.trends.api.dto.category

import jakarta.validation.constraints.NotEmpty
import java.util.UUID

data class SubmitUserCategoriesRequest(
    @field:NotEmpty(message = "User Categories cannot be empty")
    val categoryIds: List<UUID>,
)