package net.thechance.faith.api.dto.nearestMosque

import net.thechance.faith.entity.Mosque
import java.time.Instant
import java.util.*

fun MosqueRequest.toMosque(imageUrl: String): Mosque {
    return Mosque(
        id = UUID.randomUUID(),
        name = name,
        address = address,
        latitude = latitude,
        longitude = longitude,
        createdAt = Instant.now(),
        imageUrl = imageUrl,
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

