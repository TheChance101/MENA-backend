package net.thechance.dukan.api.dto.dukan

import jakarta.validation.constraints.Size

data class DeactivateDukanRequest(
    @field:Size(max = 200, message = "name must not exceed 200 characters")
    val reason: String
)
