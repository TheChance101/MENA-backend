package net.thechance.wallet.service.mapper

import net.thechance.events.wallet.TransactionCompletedEvent
import net.thechance.wallet.entity.Transaction

fun Transaction.toTransactionCompletedEvent(): TransactionCompletedEvent {
    return TransactionCompletedEvent(
        transactionId = id,
        createdAt = createdAt,
        status = status.toEventStatus(),
        type = type.toEventType(),
        senderId = sender.userId,
        receiverId = receiver.userId,
        amount = amount
    )
}

private fun Transaction.Status.toEventStatus(): TransactionCompletedEvent.TransactionStatus =
    when (this) {
        Transaction.Status.FAILED -> TransactionCompletedEvent.TransactionStatus.FAILED
        Transaction.Status.SUCCESS -> TransactionCompletedEvent.TransactionStatus.SUCCESS
        Transaction.Status.PENDING -> TransactionCompletedEvent.TransactionStatus.PENDING
    }

private fun Transaction.Type.toEventType(): TransactionCompletedEvent.TransactionType =
    when (this) {
        Transaction.Type.P2P -> TransactionCompletedEvent.TransactionType.P2P
        Transaction.Type.ONLINE_PURCHASE -> TransactionCompletedEvent.TransactionType.ONLINE_PURCHASE
        Transaction.Type.DEPOSIT -> TransactionCompletedEvent.TransactionType.DEPOSIT
    }
