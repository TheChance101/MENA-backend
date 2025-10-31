package net.thechance.dukan.api.dto.cart

import com.fasterxml.jackson.annotation.JsonProperty
import java.util.*

data class CartResponse(
    @field:JsonProperty("id")
    val id: UUID,
    @field:JsonProperty("total_price")
    val totalPrice: Double,
)