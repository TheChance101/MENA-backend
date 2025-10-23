package net.thechance.wallet.service.utils

import java.time.LocalDateTime

fun LocalDateTime?.orNow() = this ?: LocalDateTime.now()