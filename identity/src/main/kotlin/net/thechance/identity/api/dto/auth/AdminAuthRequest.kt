package net.thechance.identity.api.dto.auth

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class AdminAuthRequest(
    @field:NotBlank(message = "username must not be blank")
    val username: String,

    @field:NotBlank(message = "password must not be blank")
    @field:Size(min = 8, message = "password must be at least 8 characters")
    val password: String
)