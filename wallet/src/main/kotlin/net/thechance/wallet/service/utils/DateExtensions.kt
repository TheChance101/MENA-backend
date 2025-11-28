package net.thechance.wallet.service.utils

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId

fun LocalDateTime?.orNow(): LocalDateTime = this ?: LocalDateTime.now()

fun LocalDate.atEndOfDay(): LocalDateTime = this.atTime(23, 59, 59, 999_999_999)

fun LocalDateTime.toServerZone(zoneId: ZoneId): LocalDateTime {
    val serverTimezone = ZoneId.systemDefault()
    return this
        .atZone(zoneId)
        .withZoneSameInstant(serverTimezone)
        .toLocalDateTime()
}

fun LocalDateTime.toClientZone(zoneId: ZoneId): LocalDateTime {
    val serverTimezone = ZoneId.systemDefault()
    return this
        .atZone(serverTimezone)
        .withZoneSameInstant(zoneId)
        .toLocalDateTime()
}