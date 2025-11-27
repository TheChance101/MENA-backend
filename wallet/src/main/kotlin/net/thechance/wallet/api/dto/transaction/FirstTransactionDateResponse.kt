package net.thechance.wallet.api.dto.transaction

import net.thechance.wallet.service.utils.toClientZone
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId

data class FirstTransactionDateResponse(
    val firstTransactionDate: LocalDate?,
)

fun LocalDateTime?.toFirstTransactionDateResponse(timezone: ZoneId): FirstTransactionDateResponse {
    return FirstTransactionDateResponse(this?.toClientZone(timezone)?.toLocalDate())
}