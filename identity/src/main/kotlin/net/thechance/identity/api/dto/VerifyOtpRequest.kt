package net.thechance.identity.api.dto

import jakarta.validation.constraints.NotBlank
import org.hibernate.validator.constraints.Length
import org.hibernate.validator.constraints.UUID

data class VerifyOtpRequest(
    @field:Length(min = 6, max = 6, message = "otp must be 6 digits")
    val otp: String,
    @field:NotBlank(message = "sessionId must not be blank")
    @field:UUID(message = "sessionId must be in a valid UUID format")
    val sessionId: String,
)