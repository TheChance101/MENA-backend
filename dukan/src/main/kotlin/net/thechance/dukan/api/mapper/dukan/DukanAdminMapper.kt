package net.thechance.dukan.api.mapper.dukan

import net.thechance.dukan.api.dto.dukan.DukanAdminResponse
import net.thechance.dukan.api.mapper.category.toDto
import net.thechance.dukan.entity.Dukan

fun Dukan.toAdminResponse(language: String): DukanAdminResponse {
    return DukanAdminResponse(
        id = id,
        name = name,
        imageUrl = imageUrl,
        address = address,
        latitude = latitude,
        longitude = longitude,
        status = status,
        activationStatus = activationStatus,
        createdAt = createdAt,
        color = color,
        style = style,
        categories = categories.map{it.toDto(language)},
    )
}
