package net.thechance.dukan.api.dto

import jakarta.validation.constraints.DecimalMax
import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import net.thechance.dukan.entity.Dukan
import java.util.*

data class DukanCreationRequest(
    @field:NotBlank
    @field:Size(max = 50, message = "name must not exceed 50 characters")
    val name: String,
    @field:Size(min = 1, max = 3, message = "must have between 1 and 3 categories")
    val categoryIds: Set<UUID>,
    @field:NotBlank
    @field:Size(max = 200, message = "address must not exceed 200 characters")
    val address: String,
    @field:DecimalMin("-90.0", message = "latitude must be >= -90")
    @field:DecimalMax("90.0", message = "latitude must be <= 90")
    val latitude: Double,
    @field:DecimalMin("-180.0", message = "longitude must be >= -180")
    @field:DecimalMax("180.0", message = "longitude must be <= 180")
    val longitude: Double,
    val colorId: UUID,
    val style: Dukan.Style,
)
