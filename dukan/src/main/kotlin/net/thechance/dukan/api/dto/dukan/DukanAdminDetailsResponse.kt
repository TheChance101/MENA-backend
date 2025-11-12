package net.thechance.dukan.api.dto.dukan

import net.thechance.dukan.api.dto.category.DukanCategoryDto
import net.thechance.dukan.entity.Dukan.Style
import net.thechance.dukan.entity.DukanColor
import java.util.*

data class DukanAdminDetailsResponse(
    val id: UUID,
    val name: String,
    val imageUrl: String,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val color: DukanColor,
    val style: Style,
    val categories: List<DukanCategoryDto>,
)