package net.thechance.wallet.api.dto.transaction

import net.thechance.wallet.entity.Transaction
import net.thechance.wallet.service.helper.PendingTransactionParams
import java.util.*

data class CreatePendingTransactionRequest(
    val type: Transaction.Type,
    val receiverId: UUID,
    val amount: Double,
)

fun CreatePendingTransactionRequest.toCreatePendingTransactionParams(
    userId: UUID
): PendingTransactionParams {
    return PendingTransactionParams(
        type = type,
        senderId = userId,
        receiverId = receiverId,
        amount = amount,
    )
}