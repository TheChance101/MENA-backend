package net.thechance.trends.api.dto.trend

import jakarta.validation.constraints.NotEmpty
import java.util.*

data class UpdateTrendRequest(
    val description: String,

    @field:NotEmpty(message = "At least one category must be selected")
    val categoryIds: Set<UUID>
)