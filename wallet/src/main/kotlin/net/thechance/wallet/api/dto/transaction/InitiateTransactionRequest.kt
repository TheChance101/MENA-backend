package net.thechance.wallet.api.dto.transaction

import net.thechance.wallet.entity.InitiateTransactionParams
import net.thechance.wallet.entity.Transaction
import java.util.UUID

data class InitiateTransactionRequest(
    val receiverId: UUID,
    val amount: Double,
)

fun InitiateTransactionRequest.toInitiateTransactionParam(
    senderId: UUID,
): InitiateTransactionParams {
    return InitiateTransactionParams(
        type = Transaction.Type.P2P,
        senderId = senderId,
        receiverId = receiverId,
        amount = amount,
    )
}