package net.thechance.wallet.api.controller.util

import java.math.BigDecimal
import java.time.LocalDate

data class StatementMetadata(
    val startDate: LocalDate,
    val endDate: LocalDate,
    val totalInflows: BigDecimal,
    val totalOutflows: BigDecimal
)
