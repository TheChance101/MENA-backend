package net.thechance.dukan.api.dto.dukan

import jakarta.validation.constraints.Size
import net.thechance.dukan.entity.Dukan

data class UpdateDukanStatusRequest(
    val status: Dukan.Status,
    @field:Size(max = 200, message = "name must not exceed 200 characters")
    val reason: String? = null
)
