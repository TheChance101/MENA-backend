package net.thechance.dukan.mapper

import net.thechance.dukan.api.dto.DukanCreationRequest
import net.thechance.dukan.entity.Dukan
import net.thechance.dukan.entity.DukanCategory
import net.thechance.dukan.entity.DukanColor
import net.thechance.dukan.service.model.DukanCreationParams
import java.util.*

fun DukanCreationRequest.toDukanCreationParams(ownerId: UUID) = DukanCreationParams(
    name = name,
    ownerId = ownerId,
    categoryIds = categoryIds,
    address = address,
    latitude = latitude,
    longitude = longitude,
    colorId = colorId,
    style = style
)

fun DukanCreationParams.toDukan(
    color: DukanColor,
    categories: Set<DukanCategory>,
): Dukan {
    return Dukan(
        name = name,
        categories = categories,
        address = address,
        latitude = latitude,
        longitude = longitude,
        color = color,
        style = style,
        ownerId = ownerId,
    )
}