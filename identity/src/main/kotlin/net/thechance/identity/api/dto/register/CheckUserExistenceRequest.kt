package net.thechance.identity.api.dto.register

import jakarta.validation.constraints.NotBlank

data class CheckUserExistenceRequest (
    @field:NotBlank(message = "username must not be blank")
    val username: String,
)