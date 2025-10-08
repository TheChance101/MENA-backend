package net.thechance.dukan.mapper

import net.thechance.dukan.api.dto.DukanDetailsResponse
import net.thechance.dukan.entity.Dukan

fun Dukan.toResponse(): DukanDetailsResponse{
    return DukanDetailsResponse(
        id = id,
        ownerId = ownerId,
        name = name,
        imageUrl = imageUrl.orEmpty(),
        address = address,
        latitude = latitude,
        longitude = longitude,
        color = color,
        style =style
    )
}