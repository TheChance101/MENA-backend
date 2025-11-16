package net.thechance.events.dukan

import net.thechance.events.MenaEvent
import java.math.BigDecimal
import java.util.UUID

data class OrderCreationEvent(
    val orderId: UUID,
    val userId: UUID,
    val dukanId: UUID,
    val dukanOwnerId: UUID,
    val totalProducts: Int,
    val totalPrice: BigDecimal,
    val deliverToAddress: String
) : MenaEvent
