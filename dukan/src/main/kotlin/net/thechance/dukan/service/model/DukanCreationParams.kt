package net.thechance.dukan.service.model

import net.thechance.dukan.entity.Dukan
import java.util.UUID

data class DukanCreationParams(
    val name: String,
    val ownerId: UUID,
    val categoryIds: Set<UUID>,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val colorId: UUID,
    val style: Dukan.Style,
)