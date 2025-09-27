package net.thechance.wallet.service.util.helperFunctions

import net.thechance.wallet.api.dto.TransactionDetailsResponse
import net.thechance.wallet.entity.Transaction
import net.thechance.wallet.entity.TransactionStatus

fun Transaction.toDetailsDto(): TransactionDetailsResponse {
    val statusString = when (this.status) {
        TransactionStatus.COMPLETED -> "Success"
        TransactionStatus.FAILED -> "Failed"
    }

    val formattedId = "TX-${this.id.toString().substring(0, 6).uppercase()}"
    val receiverName = "User - ${this.receiverId}"

    return TransactionDetailsResponse(
        amount = this.amount,
        status = statusString,
        type = "Purchase",
        receiverName = receiverName,
        date = this.createdAt,
        transactionId = formattedId
    )
}