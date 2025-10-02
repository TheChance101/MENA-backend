package net.thechance.wallet.api.dto.transaction

import net.thechance.wallet.service.helper.UserTransactionType
import java.time.LocalDateTime
import java.util.*

data class StatementData(
    val userId: UUID,
    val username: String,
    val startDateTime: LocalDateTime,
    val endDateTime: LocalDateTime,
    val openingBalance: Double,
    val closingBalance: Double,
    val types: List<UserTransactionType>?,
)