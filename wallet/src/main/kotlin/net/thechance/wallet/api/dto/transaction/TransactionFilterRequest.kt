package net.thechance.wallet.api.dto.transaction

import net.thechance.wallet.entity.Transaction
import java.time.LocalDateTime

data class TransactionFilterRequest (
    val type: TransactionType? = null,
    val status: Transaction.Status? = null,
    val startDate: LocalDateTime? = null,
    val endDate: LocalDateTime? = null
)