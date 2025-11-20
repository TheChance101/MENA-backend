package net.thechance.dukan.api.mapper.order

import net.thechance.dukan.api.dto.order.OrderItemResponse
import net.thechance.dukan.entity.OrderItem
import net.thechance.dukan.entity.Price

fun OrderItem.toResponse(): OrderItemResponse {
    return OrderItemResponse(
        productId = productId,
        productName = productImage,
        quantity = quantity,
        price = Price(priceBeforeDiscount, priceAfterDiscount),
        imageUrl = productImage
    )
}