package net.thechance.dukan.api.dto.order

import net.thechance.dukan.entity.Price
import java.util.*

data class OrderItemResponse(
    val productId: UUID,
    val productName: String,
    val quantity: Int,
    val price: Price,
    val imageUrl: String?
)