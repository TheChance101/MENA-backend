package net.thechance.dukan.api.dto.dukan

import net.thechance.dukan.api.dto.category.DukanCategoryDto

data class DukanAdminDetailsResponse(
    val dukan: DukanDetailsResponse,
    val categories: List<DukanCategoryDto>,
)