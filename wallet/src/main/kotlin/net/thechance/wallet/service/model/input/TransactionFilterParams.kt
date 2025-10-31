package net.thechance.wallet.service.model.input

import net.thechance.wallet.entity.Transaction
import java.time.LocalDate

data class TransactionFilterParams(
    val types: List<UserTransactionType>?,
    val status: Transaction.Status?,
    val startDate: LocalDate?,
    val endDate: LocalDate?
)