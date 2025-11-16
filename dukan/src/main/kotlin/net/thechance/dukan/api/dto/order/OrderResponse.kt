package net.thechance.dukan.api.dto.order

import java.math.BigDecimal

class OrderResponse(
    val time: String,
    val orderItemResponse: List<OrderItemResponse>,
    val discount: BigDecimal,
    val platformFees: BigDecimal,
    val totalAmount: BigDecimal,
    val addersLine: String,
    val customerName: String,
    val customerNumber: String,
    val customerImage: String? = null,
)