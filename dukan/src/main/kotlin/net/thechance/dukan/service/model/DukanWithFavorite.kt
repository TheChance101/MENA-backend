package net.thechance.dukan.service.model

import net.thechance.dukan.entity.Dukan

data class DukanWithFavorite(
    val dukan: Dukan,
    val isFavorite: Boolean
)