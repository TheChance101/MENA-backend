package net.thechance.wallet.service.mapper

import net.thechance.events.wallet.TransactionCompletedEvent
import net.thechance.wallet.entity.Transaction

fun Transaction.toTransactionCompletedEvent(): TransactionCompletedEvent {
    return TransactionCompletedEvent(
        transactionId = this.id,
        createdAt = this.createdAt,
        status = when (this.status) {
            Transaction.Status.FAILED -> TransactionCompletedEvent.TransactionStatus.FAILED
            Transaction.Status.SUCCESS -> TransactionCompletedEvent.TransactionStatus.SUCCESS
            Transaction.Status.PENDING -> TransactionCompletedEvent.TransactionStatus.PENDING
        },
        type = when (this.type) {
            Transaction.Type.P2P -> TransactionCompletedEvent.TransactionType.P2P
            Transaction.Type.ONLINE_PURCHASE -> TransactionCompletedEvent.TransactionType.ONLINE_PURCHASE
        },
        senderId = this.sender.userId,
        receiverId = this.receiver.userId,
        amount = this.amount
    )
}