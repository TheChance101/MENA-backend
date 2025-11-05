package net.thechance.dukan.service.model

import net.thechance.dukan.entity.DukanProduct

data class DukanProductWithFavoriteAndQuantity(
    val product: DukanProduct,
    val isFavorite: Boolean,
    val quantity: Int
)