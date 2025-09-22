package net.thechance.wallet.service.util.helperFunctions

import net.thechance.wallet.api.dto.TransactionHistoryResponse
import net.thechance.wallet.api.dto.TransactionType
import net.thechance.wallet.entity.Transaction
import java.util.*

fun Transaction.toDto(currentUserId: UUID): TransactionHistoryResponse {
    val isSender = this.senderId == currentUserId
    val type = if (isSender) TransactionType.SENT else TransactionType.RECEIVED
    val counterpartyId = if (isSender) this.receiverId else this.senderId

    val counterpartyName = "User $counterpartyId"

    return TransactionHistoryResponse(
        transactionId = this.id,
        type = type,
        description = this.description,
        status = this.status,
        amount = this.amount,
        date = this.createdAt,
        counterpartyName = counterpartyName
    )
}