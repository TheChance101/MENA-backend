package net.thechance.dukan.api.dto.product

import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.NotNull
import java.math.BigDecimal

data class PriceRequest(
    @field:NotNull(message = "base price is required")
    @field:DecimalMin(value = "0.001", inclusive = true, message = "base price must be >= 0.001")
    val base: BigDecimal,

    @field:DecimalMin(value = "0.001", inclusive = true, message = "final price must be >= 0.001")
    val final: BigDecimal? =null
)
