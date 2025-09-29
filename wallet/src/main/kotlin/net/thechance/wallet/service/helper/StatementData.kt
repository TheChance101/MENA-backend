package net.thechance.wallet.service.helper

import net.thechance.wallet.api.dto.transaction.UserTransactionType
import net.thechance.wallet.entity.Transaction
import java.time.LocalDateTime
import java.util.*

data class StatementData(
    val userId: UUID,
    val username: String,
    val startDateTime: LocalDateTime,
    val endDateTime: LocalDateTime,
    val openingBalance: Double,
    val closingBalance: Double,
    val totalPages: Int,
    val types: List<UserTransactionType>?,
    val transactionProvider: (pageNum: Int) -> List<Transaction>
)