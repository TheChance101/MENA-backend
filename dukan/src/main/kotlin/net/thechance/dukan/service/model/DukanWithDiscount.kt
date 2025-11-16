package net.thechance.dukan.service.model

import net.thechance.dukan.entity.Dukan
import java.math.BigDecimal

data class DukanWithDiscount(
    val dukan: Dukan,
    val discount: BigDecimal
)
