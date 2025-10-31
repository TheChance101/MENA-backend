package net.thechance.dukan.api.dto.cart

import com.fasterxml.jackson.annotation.JsonProperty
import java.util.*

data class CartResponse(
    val id: UUID,
    val totalPrice: Double,
)