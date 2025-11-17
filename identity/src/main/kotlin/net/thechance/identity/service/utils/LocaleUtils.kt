package net.thechance.identity.service.utils

import java.awt.ComponentOrientation
import java.util.*

fun Locale.isRtl(): Boolean {
    return ComponentOrientation.getOrientation(this).isLeftToRight.not()
}