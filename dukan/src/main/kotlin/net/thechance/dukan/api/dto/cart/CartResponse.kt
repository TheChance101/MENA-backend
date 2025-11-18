package net.thechance.dukan.api.dto.cart

import com.fasterxml.jackson.annotation.JsonProperty
import java.math.BigDecimal
import java.util.*

data class CartResponse(
    val id: UUID,
    val totalPriceBeforeDiscount: BigDecimal,
    val totalPriceAfterDiscount: BigDecimal,
    val discount : BigDecimal = BigDecimal.ZERO,
)