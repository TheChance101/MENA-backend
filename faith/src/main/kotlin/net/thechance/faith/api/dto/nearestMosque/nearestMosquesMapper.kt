package net.thechance.faith.api.dto.nearestMosque

import net.thechance.faith.entity.Mosque
import java.time.Instant

fun MosqueRequest.toMosque(): Mosque {
    return Mosque(
        name = name,
        address = address,
        latitude = latitude,
        longitude = longitude,
        imageUrl = imageUrl,
        createdAt = Instant.now(),
    )
}

fun Mosque.toMosqueResponse(): MosqueResponse {
    return MosqueResponse(
        id = id,
        name = name,
        address = address,
        latitude = latitude,
        longitude = longitude,
        imageUrl = imageUrl,
        createdAt = createdAt
    )
}

