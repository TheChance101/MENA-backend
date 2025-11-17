package net.thechance.dukan.api.mapper.order

import net.thechance.dukan.api.dto.order.OrderResponse
import net.thechance.dukan.entity.Order
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

fun Order.toResponse(isDukanOwner: Boolean): OrderResponse {
    return OrderResponse(
        orderId = id,
        orderNumber = orderNumber,
        time = createdAt.toDateAsString(),
        orderItemResponse = items.map { it.toResponse() },
        discount = discountPercentage,
        platformFees = platformFees,
        totalAmount = totalAfterDiscount,
        addersLine = deliveryAddress,
        customerLatitude = deliveryLat,
        customerLongitude = deliveryLng,
        customerName = customerName,
        customerNumber = customerPhone,
        customerImage = customerImage,
        dukanLatitude = dukanLatitude,
        dukanLongitude = dukanLongitude,
        isDukanOwner = isDukanOwner,
    )
}

fun Instant.toDateAsString(): String {
    return this
        .atZone(ZoneId.systemDefault())
        .toLocalDate()
        .format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
}