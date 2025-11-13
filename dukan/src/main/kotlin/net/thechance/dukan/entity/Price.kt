package net.thechance.dukan.entity

import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import java.math.BigDecimal

@Embeddable
data class Price(
    @Column(name = "base_price", nullable = false, precision = 10, scale = 2)
    val base: BigDecimal,

    @Column(name = "final_price", nullable = false, precision = 10, scale = 2)
    val final: BigDecimal,
)

