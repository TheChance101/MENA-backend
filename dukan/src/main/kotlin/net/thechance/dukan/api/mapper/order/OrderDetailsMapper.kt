package net.thechance.dukan.api.mapper.order

import net.thechance.dukan.api.dto.order.OrderResponse
import net.thechance.dukan.entity.Order
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

fun Order.toResponse(isDukanOwner: Boolean): OrderResponse {
    return OrderResponse(
        time = createdAt.toDateAsString(),
        orderItemResponse = items.map { it.toResponse() },
        discount = discountPercentage,
        platformFees = platformFees,
        totalAmount = totalAfterDiscount,
        addersLine = deliveryAddress,
        latitude = deliveryLat,
        longitude = deliveryLng,
        customerName = customerName,
        customerNumber = customerPhone,
        customerImage = customerImage,
        isDukanOwner = isDukanOwner
    )
}

fun Instant.toDateAsString(): String {
    return this
        .atZone(ZoneId.systemDefault())
        .toLocalDate()
        .format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
}