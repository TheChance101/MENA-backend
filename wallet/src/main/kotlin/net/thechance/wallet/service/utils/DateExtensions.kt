package net.thechance.wallet.service.utils

import java.time.LocalDate
import java.time.LocalDateTime

fun LocalDateTime?.orNow(): LocalDateTime = this ?: LocalDateTime.now()

fun LocalDate?.atEndOfDay() = this?.atTime(23, 59, 59, 59)