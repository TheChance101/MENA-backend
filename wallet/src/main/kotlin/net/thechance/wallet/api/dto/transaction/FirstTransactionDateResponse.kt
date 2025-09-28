package net.thechance.wallet.api.dto.transaction

import java.time.LocalDateTime

data class FirstTransactionDateResponse(
    val firstTransactionDate: LocalDateTime?,
)
