package net.thechance.chat.eventListener.mapper

import net.thechance.chat.entity.Message
import net.thechance.chat.service.model.MessageContent
import net.thechance.events.dukan.OrderCreationEvent
import java.util.UUID

fun OrderCreationEvent.toOrderMessage(chatId: UUID): Message {
    return Message(
        chatId = chatId,
        senderId = dukanOwnerId,
        type = Message.MessageType.ORDER,
        content = MessageContent.Order(
            orderId = orderId.toString(),
            totalProducts = totalProducts,
            totalPrice = totalPrice.toString(),
            deliverToAddress = deliverToAddress,
            dukanId = dukanId.toString(),
            dukanOwnerId = dukanOwnerId.toString()
        )
    )
}