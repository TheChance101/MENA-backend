package net.thechance.wallet.service.model.input

import net.thechance.wallet.entity.Transaction
import java.time.LocalDateTime

data class TransactionFilterParams(
    val types: List<UserTransactionType>?,
    val status: Transaction.Status?,
    val startDateTime: LocalDateTime?,
    val endDateTime: LocalDateTime?
)