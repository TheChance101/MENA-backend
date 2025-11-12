package net.thechance.dukan.api.dto.dukan

import net.thechance.dukan.api.dto.category.DukanCategoryDto
import net.thechance.dukan.entity.Dukan
import net.thechance.dukan.entity.Dukan.Style
import net.thechance.dukan.entity.DukanColor
import java.time.Instant
import java.util.*

data class DukanAdminResponse(
    val id: UUID,
    val name: String,
    val imageUrl: String?,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val status: Dukan.Status,
    val activationStatus: Dukan.ActivationStatus?,
    val createdAt: Instant,
    val color: DukanColor,
    val style: Style,
    val categories: List<DukanCategoryDto>
)
