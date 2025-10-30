package net.thechance.identity.api.dto

import org.hibernate.validator.constraints.Length

data class ChangePasswordRequest(
    val currentPassword: String,

    @field:Length(min = 8, message = "password must be more than or equals 8 characters")
    val newPassword: String,

    @field:Length(min = 8, message = "confirm password must be more than or equals 8 characters")
    val confirmPassword: String,
)

data class ChangePasswordResponse(val message: String)
