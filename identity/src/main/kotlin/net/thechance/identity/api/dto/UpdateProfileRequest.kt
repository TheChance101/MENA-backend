package net.thechance.identity.api.dto

import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min

data class UpdateProfileRequest (
    val username: String,
    val firstName: String,
    val lastName: String,
    val imageUrl: String?,
    val birthDate: String,
    @field:Min(1, message = "Gender must be 1 or 2")
    @field:Max(2, message = "Gender must be 1 or 2")
    val gender: Int,
)