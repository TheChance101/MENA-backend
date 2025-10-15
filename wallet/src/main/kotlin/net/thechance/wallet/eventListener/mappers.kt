package net.thechance.wallet.eventListener

import net.thechance.events.wallet.TransactionInitiatedEvent
import net.thechance.wallet.entity.InitiateTransactionParams
import net.thechance.wallet.entity.Transaction

fun TransactionInitiatedEvent.toInitiateTransactionParams(): InitiateTransactionParams {
    return InitiateTransactionParams(
        type = type.toTransactionType(),
        senderId = senderId,
        receiverId = receiverId,
        amount = amount
    )
}

fun TransactionInitiatedEvent.TransactionType.toTransactionType(): Transaction.Type {
    return when (this) {
        TransactionInitiatedEvent.TransactionType.P2P -> Transaction.Type.P2P
        TransactionInitiatedEvent.TransactionType.ONLINE_PURCHASE -> Transaction.Type.ONLINE_PURCHASE
    }
}