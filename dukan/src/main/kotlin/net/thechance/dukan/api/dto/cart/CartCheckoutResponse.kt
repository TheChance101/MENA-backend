package net.thechance.dukan.api.dto.cart

import java.util.UUID

data class CartCheckoutResponse(
    val transactionId: UUID,
    val totalAmount: Double
)
