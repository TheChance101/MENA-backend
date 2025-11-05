package net.thechance.dukan.api.dto.cart

import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotNull
import java.util.*

data class AddToCartRequest(
    @field:NotNull(message = "dukanId is required")
    val dukanId: UUID,
    @field:NotNull(message = "productId is required")
    val productId: UUID,
    @field:Min(1, message = "quantity must be at least 1")
    val quantity: Int
)