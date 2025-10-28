package net.thechance.dukan.api.dto.cart

import com.fasterxml.jackson.annotation.JsonProperty
import java.util.*

data class CartItemResponse(
    @field:JsonProperty("product_id")
    val productId: UUID,
    @field:JsonProperty("product_name")
    val productName: String,
    @field:JsonProperty("description")
    val description: String,
    @field:JsonProperty("quantity")
    val quantity: Int,
    @field:JsonProperty("price")
    val price: Double,
    @field:JsonProperty("image_url")
    val imageUrl: String?
)