package net.thechance.dukan.api.mapper.dukan

import net.thechance.dukan.api.dto.dukan.DukanResponse
import net.thechance.dukan.entity.Dukan

fun Dukan.toDukanResponse(isFavorite: Boolean = false): DukanResponse {
    return DukanResponse(
        id =  id,
        name = name,
        imageUrl = imageUrl.orEmpty(),
        isFavorite = isFavorite
    )
}