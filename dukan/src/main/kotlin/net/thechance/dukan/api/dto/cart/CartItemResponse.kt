package net.thechance.dukan.api.dto.cart

import com.fasterxml.jackson.annotation.JsonProperty
import java.util.*

data class CartItemResponse(
    val productId: UUID,
    val productName: String,
    val description: String,
    val quantity: Int,
    val price: Double,
    val imageUrl: String?
)