package net.thechance.identity.api.dto

import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import net.thechance.identity.service.model.UserServiceModel
import org.hibernate.validator.constraints.Length
import java.util.*

data class UpdateProfileRequest(
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
    val imageUrl: String?,
    val updateImage: Boolean = false
)