package net.thechance.wallet.api.dto.transaction

import net.thechance.wallet.repository.transaction.TransactionProjection

fun TransactionProjection.toResponse(): TransactionResponse {

    return TransactionResponse(
        id = id,
        senderId = senderId,
        senderName = senderName,
        receiverId = receiverId,
        receiverName = receiverName,
        status = status,
        type = TransactionType.valueOf(type),
        createdAt = createdAt,
        amount = amount,
    )
}