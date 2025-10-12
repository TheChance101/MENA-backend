package net.thechance.identity.mapper.util

import java.time.LocalDate
import java.time.format.DateTimeFormatter

const val DEFAULT_DATE_FORMAT = "yyyy-MM-dd"
fun LocalDate.formatAsString(): String {
    return format(DateTimeFormatter.ofPattern(DEFAULT_DATE_FORMAT))
}