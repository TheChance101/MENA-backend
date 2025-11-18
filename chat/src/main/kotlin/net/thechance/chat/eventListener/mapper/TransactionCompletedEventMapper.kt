package net.thechance.chat.eventListener.mapper

import net.thechance.chat.entity.Message
import net.thechance.chat.service.model.MessageContent
import net.thechance.events.wallet.TransactionCompletedEvent
import java.util.UUID

fun TransactionCompletedEvent.toMessage(chatId: UUID): Message {
    return Message(
        chatId = chatId,
        senderId = senderId,
        type = Message.MessageType.MONEY,
        content = MessageContent.Money(amount = amount.toDouble()),
    )
}