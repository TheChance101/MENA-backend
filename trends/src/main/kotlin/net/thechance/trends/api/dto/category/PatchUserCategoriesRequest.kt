package net.thechance.trends.api.dto.category

import java.util.*

data class PatchUserCategoriesRequest(
    val add: List<UUID> = emptyList(),
    val remove: List<UUID> = emptyList()
)