package net.thechance.dukan.service.model

import java.util.UUID

data class AddOrUpdateCartItemParams(
    val userId: UUID,
    val dukanId: UUID,
    val productId: UUID,
    val quantity: Int
)
