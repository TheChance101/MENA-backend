package net.thechance.dukan.api.mapper.dukan

import net.thechance.dukan.api.dto.dukan.DukanDetailsResponse
import net.thechance.dukan.api.mapper.category.toDto
import net.thechance.dukan.entity.Dukan

fun Dukan.toResponse(isFavorite: Boolean): DukanDetailsResponse{
    return DukanDetailsResponse(
        id = id,
        ownerId = ownerId,
        name = name,
        imageUrl = imageUrl.orEmpty(),
        address = address,
        latitude = latitude,
        longitude = longitude,
        color = color,
        style =style,
        isFavorite = isFavorite
    )
}