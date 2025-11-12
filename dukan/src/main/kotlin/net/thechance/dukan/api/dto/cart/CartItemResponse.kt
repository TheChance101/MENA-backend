package net.thechance.dukan.api.dto.cart

import net.thechance.dukan.entity.Price
import java.util.*

data class CartItemResponse(
    val productId: UUID,
    val productName: String,
    val description: String,
    val quantity: Int,
    val price: Price,
    val imageUrl: String?
)