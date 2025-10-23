package net.thechance.wallet.api.dto.transaction

import net.thechance.wallet.entity.Transaction
import net.thechance.wallet.service.model.input.UserTransactionType
import net.thechance.wallet.service.model.output.TransactionDetailsModel
import java.util.*

private fun getUserTypeAndReceiverName(
    type: Transaction.Type,
    senderUserId: UUID,
    receiverUserName: String,
    receiverDukanName: String?,
    currentUserId: UUID
): Pair<UserTransactionType, String> {
    val userType = when (type) {
        Transaction.Type.P2P -> {
            if (senderUserId == currentUserId) UserTransactionType.SENT
            else UserTransactionType.RECEIVED
        }
        Transaction.Type.ONLINE_PURCHASE -> {
            if (senderUserId == currentUserId) UserTransactionType.ONLINE_PURCHASE
            else UserTransactionType.RECEIVED
        }
    }

    val actualReceiverName =
        if (userType == UserTransactionType.ONLINE_PURCHASE)
            receiverDukanName?.ifBlank { receiverUserName } ?: receiverUserName
        else
            receiverUserName

    return userType to actualReceiverName
}

fun Transaction.toResponse(currentUserId: UUID): TransactionResponse {
    val (userType, actualReceiverName) = getUserTypeAndReceiverName(
        type = type,
        senderUserId = sender.userId,
        receiverUserName = receiver.userName,
        receiverDukanName = receiver.dukan?.name,
        currentUserId = currentUserId
    )

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

fun TransactionDetailsModel.toResponse(currentUserId: UUID): TransactionResponse {
    val (userType, actualReceiverName) = getUserTypeAndReceiverName(
        type = type,
        senderUserId = sender.userId,
        receiverUserName = receiver.userName,
        receiverDukanName = receiver.dukan?.name,
        currentUserId = currentUserId
    )

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