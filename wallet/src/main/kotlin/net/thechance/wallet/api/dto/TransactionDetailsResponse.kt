package net.thechance.wallet.api.dto

import java.math.BigDecimal
import java.time.LocalDateTime

data class TransactionDetailsResponse(
    val amount: BigDecimal,
    val status: String,
    val type: String,
    val receiverName: String,
    val date: LocalDateTime,
    val transactionId: String
)