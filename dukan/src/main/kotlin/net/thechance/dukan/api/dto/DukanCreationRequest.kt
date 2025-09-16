package net.thechance.dukan.api.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import net.thechance.dukan.entity.Dukan
import java.util.*

data class DukanCreationRequest(
    @field:NotBlank
    val name: String,
    @field:Size(min = 1, max = 3, message = "must have between 1 and 3 categories")
    val categoryIds: Set<UUID>,
    @field:NotBlank
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val colorId: UUID,
    val style: Dukan.Style,
)
