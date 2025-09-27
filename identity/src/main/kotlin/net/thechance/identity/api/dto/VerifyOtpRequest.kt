package net.thechance.identity.api.dto

import jakarta.validation.constraints.NotBlank
import org.hibernate.validator.constraints.Length

data class VerifyOtpRequest(
    @field:NotBlank(message = "phoneNumber must not be blank")
    val phoneNumber: String,
    @field:Length(min = 6, max = 6, message = "otp must be 6 digits")
    val otp: String,
    @field:NotBlank(message = "sessionId must not be blank")
    val sessionId: String,
)