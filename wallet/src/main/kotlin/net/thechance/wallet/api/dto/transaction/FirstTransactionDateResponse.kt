package net.thechance.wallet.api.dto.transaction

import java.time.LocalDate

data class FirstTransactionDateResponse(
    val firstTransactionDate: LocalDate?,
)
