package net.thechance.faith.utils

fun Int?.orZero() = this ?: 0

fun Int.padWithThreeDigits(): String = this.toString().padStart(length = 3, padChar = '0')
