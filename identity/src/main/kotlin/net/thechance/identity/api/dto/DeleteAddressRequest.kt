package net.thechance.identity.api.dto

import jakarta.validation.constraints.NotBlank
import org.hibernate.validator.constraints.UUID

data class DeleteAddressRequest(
    @field:NotBlank(message = "addressId must not be blank")
    @field:UUID(message = "addressId must be in a valid UUID format")
    val addressId: String
)