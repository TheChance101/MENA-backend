package net.thechance.identity.api.dto

import jakarta.validation.constraints.DecimalMax
import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.NotBlank

data class UpdateAddressRequest(
    @field:DecimalMin(value = "-90.0", message = "Latitude must be at least -90.")
    @field:DecimalMax(value = "90.0", message = "Latitude must not be greater than 90.")
    val latitude: Double?,

    @field:DecimalMin(value = "-180.0", message = "Longitude must be at least -180.")
    @field:DecimalMax(value = "180.0", message = "Longitude must not be greater than 180.")
    val longitude: Double?,

    @field:NotBlank(message = "AddressLine must not be empty.")
    val addressLine: String?,

    @field:NotBlank(message = "AddressType must not be empty.")
    val addressType: String?,

    val isActive: Boolean?
)