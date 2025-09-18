package identity.api.dto

import jakarta.validation.constraints.NotBlank

data class RefreshTokenRequest(
    @field:NotBlank(message = "refreshToken must not be blank")
    val refreshToken: String
)