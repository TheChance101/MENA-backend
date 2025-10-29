package net.thechance.wallet.api.dto.transaction

import java.time.LocalDate
import java.time.LocalDateTime

data class FirstTransactionDateResponse(
    val firstTransactionDate: LocalDate?,
)

fun LocalDateTime?.toFirstTransactionDateResponse(): FirstTransactionDateResponse {
    return FirstTransactionDateResponse(firstTransactionDate = this?.toLocalDate())
}