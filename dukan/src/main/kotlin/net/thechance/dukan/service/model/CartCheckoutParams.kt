package net.thechance.dukan.service.model

import java.util.*

data class CartCheckoutParams(
    val cartId: UUID,
    val address: String,
    val longitude: Double,
    val latitude: Double
)