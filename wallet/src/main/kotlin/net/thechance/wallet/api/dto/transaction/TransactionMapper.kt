package net.thechance.wallet.api.dto.transaction

import net.thechance.wallet.entity.Transaction
import java.util.*

fun Transaction.toResponse(currentUserId: UUID): TransactionResponse {
    val userType = when (type) {
        Transaction.Type.P2P -> {
            if (sender.userId == currentUserId) UserTransactionType.SENT
            else UserTransactionType.RECEIVED
        }
        Transaction.Type.ONLINE_PURCHASE -> UserTransactionType.ONLINE_PURCHASE
    }

    val actualReceiverName = receiver.dukanName?.ifBlank { receiver.userName } ?: receiver.userName

    return TransactionResponse(
        id = id,
        senderName = sender.userName,
        receiverName = actualReceiverName,
        status = status,
        type = userType,
        createdAt = createdAt,
        amount = amount.toDouble()
    )
}
