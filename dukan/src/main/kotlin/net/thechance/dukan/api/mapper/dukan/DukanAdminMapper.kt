package net.thechance.dukan.api.mapper.dukan

import net.thechance.dukan.api.dto.dukan.DukanAdminResponse
import net.thechance.dukan.api.mapper.category.toDto
import net.thechance.dukan.entity.Dukan

fun Dukan.toAdminResponse(language: String): DukanAdminResponse {
    return DukanAdminResponse(
        id = id,
        name = name,
        imageUrl = imageUrl.orEmpty(),
        address = address,
        latitude = latitude,
        longitude = longitude,
        status = status,
        activationStatus = activationStatus,
        createdAt = createdAt,
        categories = categories.map{it.toDto(language)},
    )
}
