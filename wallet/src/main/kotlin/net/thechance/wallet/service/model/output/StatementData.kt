package net.thechance.wallet.service.model.output

import net.thechance.wallet.service.model.input.UserTransactionType
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