package net.thechance.wallet.api.dto.transaction

import jakarta.validation.constraints.DecimalMin
import net.thechance.wallet.entity.Transaction
import net.thechance.wallet.service.model.input.InitiateTransactionParams
import java.util.*

data class InitiateTransactionRequest(
    val receiverId: UUID,

    @field:DecimalMin(value = "0.0", inclusive = false, message = "Amount must be greater than zero")
    val amount: Double,
)

fun InitiateTransactionRequest.toInitiateTransactionParam(
    senderId: UUID,
    type: Transaction.Type,
): InitiateTransactionParams {
    return InitiateTransactionParams(
        type = type,
        senderId = senderId,
        receiverId = receiverId,
        amount = amount,
        transactionId = UUID.randomUUID()
    )
}