package net.thechance.identity.api.dto

import jakarta.validation.constraints.NotBlank
import org.hibernate.validator.constraints.Length

data class RequestOtpRequest(
    @field:NotBlank(message = "phoneNumber must not be blank")
    val phoneNumber: String,
    @field:NotBlank(message = "defaultRegion must not be blank")
    @field:Length(min = 2, max = 2, message = "defaultRegion must be 2 chars long")
    val defaultRegion: String,
)