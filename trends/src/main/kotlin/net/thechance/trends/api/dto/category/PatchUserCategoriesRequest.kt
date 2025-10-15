package net.thechance.trends.api.dto.category

import jakarta.validation.constraints.AssertTrue
import java.util.*

data class PatchUserCategoriesRequest(
    val add: List<UUID> = emptyList(),
    val remove: List<UUID> = emptyList()
) {
    @AssertTrue(message = "At least one operation (add or remove) is required")
    fun isAtLeastOneOperationPresent() = add.isNotEmpty() || remove.isNotEmpty()

    @AssertTrue(message = "Cannot add and remove the same category")
    fun hasNoOverlap() = add.intersect(remove.toSet()).isEmpty()
}
