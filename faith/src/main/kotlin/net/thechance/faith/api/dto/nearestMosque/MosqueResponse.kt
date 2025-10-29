package net.thechance.faith.api.dto.nearestMosque

import java.time.LocalDate
import java.util.UUID

data class MosqueResponse(
    val id: UUID,
    val name: String,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val imageUrl: List<String>?,
    val createdAt: LocalDate = LocalDate.now()
)