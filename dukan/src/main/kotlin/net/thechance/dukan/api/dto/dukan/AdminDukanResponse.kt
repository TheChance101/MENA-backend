package net.thechance.dukan.api.dto.dukan

import net.thechance.dukan.entity.Dukan
import java.time.Instant
import java.util.UUID

data class AdminDukanResponse(
    val id: UUID,
    val name: String,
    val imageUrl: String?,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val status: Dukan.Status,
    val activationStatus: Dukan.ActivationStatus?,
    val createdAt: Instant,
)
