package net.thechance.identity.api.dto

import jakarta.validation.constraints.NotBlank

data class AdminAuthRequest(
    @field:NotBlank(message = "username must not be blank")
    val username: String,

    @field:NotBlank(message = "password must not be blank")
    val password: String
)