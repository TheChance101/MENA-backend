package net.thechance.dukan.service.model

import net.thechance.dukan.entity.DukanProduct

data class DukanProductWithFavorite(
    val product: DukanProduct,
    val isFavorite: Boolean
)
