package net.thechance.identity.api.dto.register

import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import org.hibernate.validator.constraints.Length

data class RegisterUserRequest(
    @field:NotBlank(message = "phoneNumber must not be blank")
    val phoneNumber: String,
    @field:NotBlank(message = "username must not be blank")
    val username: String,
    @field:NotBlank(message = "firstName must not be blank")
    val firstName: String,
    @field:NotBlank(message = "lastName must not be blank")
    val lastName: String,
    @field:NotBlank(message = "birthDate must not be blank")
    val birthDate: String,
    @field:Min(1, message = "Gender must be 1 or 2")
    @field:Max(2, message = "Gender must be 1 or 2")
    val gender: Int,
    @field:NotBlank(message = "password must not be blank")
    @field:Length(min = 8, message = "password must be more than or equals 8 characters")
    val password: String
)
