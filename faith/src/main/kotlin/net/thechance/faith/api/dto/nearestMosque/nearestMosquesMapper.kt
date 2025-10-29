package net.thechance.faith.api.dto.nearestMosque

import net.thechance.faith.entity.Mosque
import java.time.LocalDate

fun MosqueRequest.toMosque(): Mosque {
    return Mosque(
        name = name,
        address = address,
        latitude = latitude,
        longitude = longitude,
        createdAt = LocalDate.now(),
    )
}

fun Mosque.toMosqueResponse(): MosqueResponse {
    return MosqueResponse(
        id = id,
        name = name,
        address = address,
        latitude = latitude,
        longitude = longitude,
        imageUrl = imageUrls,
        createdAt = createdAt
    )
}

