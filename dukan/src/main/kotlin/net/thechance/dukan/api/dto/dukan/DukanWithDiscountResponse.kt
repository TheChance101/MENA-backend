package net.thechance.dukan.api.dto.dukan

import java.math.BigDecimal
import java.util.UUID

data class DukanWithDiscountResponse(
    val id: UUID,
    val imageUrl: String,
    val discount: BigDecimal
)