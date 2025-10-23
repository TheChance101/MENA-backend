package net.thechance.wallet.eventListener.mapper

import net.thechance.events.wallet.InitiateTransactionEvent
import net.thechance.wallet.entity.Transaction
import net.thechance.wallet.service.model.input.InitiateTransactionParams

fun InitiateTransactionEvent.toInitiateTransactionParams(): InitiateTransactionParams {
    return InitiateTransactionParams(
        type = type.toTransactionType(),
        senderId = senderId,
        receiverId = receiverId,
        amount = amount
    )
}

fun InitiateTransactionEvent.TransactionType.toTransactionType(): Transaction.Type {
    return when (this) {
        InitiateTransactionEvent.TransactionType.P2P -> Transaction.Type.P2P
        InitiateTransactionEvent.TransactionType.ONLINE_PURCHASE -> Transaction.Type.ONLINE_PURCHASE
    }
}