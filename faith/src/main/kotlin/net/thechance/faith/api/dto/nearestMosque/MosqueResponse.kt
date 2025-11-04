package net.thechance.faith.api.dto.nearestMosque

import java.time.Instant
import java.util.UUID

data class MosqueResponse(
    val id: UUID,
    val name: String,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val imageUrl: String,
    val createdAt: Instant = Instant.now()
)
