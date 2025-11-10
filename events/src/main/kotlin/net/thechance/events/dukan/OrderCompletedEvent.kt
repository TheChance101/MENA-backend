package net.thechance.events.dukan

import net.thechance.events.MenaEvent
import java.util.*

data class OrderCompletedEvent(
    val orderId: UUID,
    val userId: UUID,
    val dukanId: UUID, // TODO: OR dukanOwnerId
    val totalProducts: Int,
    val totalPrice: Double,
    val address: String,
) : MenaEvent