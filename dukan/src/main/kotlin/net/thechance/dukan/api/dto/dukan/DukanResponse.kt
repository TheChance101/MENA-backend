package net.thechance.dukan.api.dto.dukan

import java.util.UUID

data class DukanResponse(
    val id: UUID,
    val name: String,
    val imageUrl: String,
    val isFavorite: Boolean
)