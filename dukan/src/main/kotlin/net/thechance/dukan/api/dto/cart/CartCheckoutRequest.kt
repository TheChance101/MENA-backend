package net.thechance.dukan.api.dto.cart

import jakarta.validation.constraints.DecimalMax
import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull
import java.util.UUID

data class CartCheckoutRequest(
    @field:NotNull(message = "cart id is required")
    val cartId: UUID,

    @field:NotNull(message = "address is required")
    @field:NotEmpty(message = "address should not be empty")
    val address: String,

    @field:NotNull(message = "longitude is required")
    @field:DecimalMin("-180.0", message = "longitude must be >= -180")
    @field:DecimalMax("180.0", message = "longitude must be <= 180")
    val longitude: Double,

    @field:NotNull(message = "latitude is required")
    @field:DecimalMin("-90.0", message = "latitude must be >= -90")
    @field:DecimalMax("90.0", message = "latitude must be <= 90")
    val latitude: Double
)
