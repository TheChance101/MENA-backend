package net.thechance.chat.eventListener.mapper

import net.thechance.chat.entity.Message
import net.thechance.chat.service.model.MessageContent
import net.thechance.events.dukan.OrderCreationEvent
import java.util.UUID

fun OrderCreationEvent.toOrderMessage(): Message {
    return Message(
        id = UUID.randomUUID(),
        senderId = this.userId,
        chatId = this.dukanId,
        type = Message.MessageType.TEXT,
        content = MessageContent.Order(
            orderId = this.orderId.toString(),
            totalProducts = this.totalProducts,
            totalPrice = this.totalPrice.toPlainString(),
            deliverToAddress = this.deliverToAddress,
            dukanId = this.dukanId.toString(),
            dukanOwnerId = this.dukanOwnerId.toString()
        )
    )
}