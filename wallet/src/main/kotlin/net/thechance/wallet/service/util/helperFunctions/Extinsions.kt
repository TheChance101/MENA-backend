package net.thechance.wallet.service.util.helperFunctions

import net.thechance.wallet.api.dto.TransactionDetailsResponse
import net.thechance.wallet.entity.Transaction
import net.thechance.wallet.entity.TransactionStatus

fun Transaction.toDetailsDto(senderName: String, receiverName: String): TransactionDetailsResponse {
    val statusString = when (this.status) {
        TransactionStatus.COMPLETED -> "Success"
        TransactionStatus.FAILED -> "Failed"
    }

    val formattedId = "TX-${this.id.toString().substring(0, 6).uppercase()}"

    return TransactionDetailsResponse(
        amount = this.amount,
        status = statusString,
        type = "Purchase",
        senderName = senderName,
        receiverName = receiverName,
        date = this.createdAt,
        transactionId = formattedId
    )
}