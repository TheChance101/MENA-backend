package net.thechance.wallet.api.dto.transaction

import net.thechance.wallet.entity.Transaction
import net.thechance.wallet.service.helper.CreatePendingTransactionParams
import java.util.*

data class CreatePendingTransactionRequest(
    val type: Transaction.Type,
    val receiverId: UUID,
    val amount: Double,
)

fun CreatePendingTransactionRequest.toCreatePendingTransactionParams(
    userId: UUID
): CreatePendingTransactionParams {
    return CreatePendingTransactionParams(
        type = type,
        senderId = userId,
        receiverId = receiverId,
        amount = amount,
    )
}