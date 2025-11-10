package net.thechance.dukan.api.mapper.dukan

import net.thechance.dukan.api.dto.dukan.AdminDukanResponse
import net.thechance.dukan.entity.Dukan

fun Dukan.toAdminResponse(): AdminDukanResponse {
    return AdminDukanResponse(
        id = id,
        name = name,
        imageUrl = imageUrl,
        address = address,
        latitude = latitude,
        longitude = longitude,
        status = status,
        createdAt = createdAt,
    )
}
