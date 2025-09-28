package net.thechance.wallet.service.helper

import net.thechance.wallet.api.dto.transaction.UserTransactionType
import net.thechance.wallet.entity.Transaction
import java.time.LocalDate

data class TransactionFilterParams(
    val types: List<UserTransactionType>,
    val status: Transaction.Status?,
    val startDate: LocalDate?,
    val endDate: LocalDate?
)
