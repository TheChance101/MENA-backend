package net.thechance.wallet.api.dto

import net.thechance.wallet.entity.TransactionStatus
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*

data class TransactionHistoryResponse(
    val transactionId: UUID,
    val type: TransactionType,
    val description: String,
    val status: TransactionStatus,
    val amount: BigDecimal,
    val date: LocalDateTime,
    val counterpartyName: String
)

enum class TransactionType {
    SENT, RECEIVED , OnlineShopping
}
