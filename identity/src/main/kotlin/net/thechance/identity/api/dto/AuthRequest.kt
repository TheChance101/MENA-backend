package net.thechance.identity.api.dto

import jakarta.validation.constraints.NotBlank
import org.hibernate.validator.constraints.Length

data class AuthRequest(
    @field:NotBlank(message = "phoneNumber must not be blank")
    val phoneNumber: String,
    @field:NotBlank(message = "password must not be blank")
    @field:Length(min = 8, message = "password must be more than 8 characters")
    val password: String
)