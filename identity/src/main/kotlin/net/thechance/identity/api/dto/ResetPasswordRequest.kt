package net.thechance.identity.api.dto

import jakarta.validation.constraints.NotBlank
import org.hibernate.validator.constraints.Length
import org.hibernate.validator.constraints.UUID

data class ResetPasswordRequest(
    @field:NotBlank(message = "password must not be blank")
    @field:Length(min = 8, message = "password must be more than or equals 8 characters")
    val newPassword: String,

    @field:NotBlank(message = "confirm password must not be blank")
    @field:Length(min = 8, message = "confirm password must be more than or equals 8 characters")
    val confirmPassword: String,

    @field:NotBlank(message = "sessionId must not be blank")
    @field:UUID(message = "sessionId must be in a valid UUID format")
    val sessionId: String
)
