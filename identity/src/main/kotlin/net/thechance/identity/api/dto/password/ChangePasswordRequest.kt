package net.thechance.identity.api.dto.password

import org.hibernate.validator.constraints.Length

data class ChangePasswordRequest(
    @field:Length(min = 8, message = "current password must be more than or equals 8 characters")
    val currentPassword: String,

    @field:Length(min = 8, message = "new password must be more than or equals 8 characters")
    val newPassword: String,

    @field:Length(min = 8, message = "confirm password must be more than or equals 8 characters")
    val confirmPassword: String,
)
