package net.thechance.dukan.service.model

import java.util.UUID

data class CartCheckoutPreview(
    val transactionId: UUID,
    val totalAmount: Double
)
