package net.thechance.dukan.api.dto

import java.util.UUID

data class DukanResponse(
    val id: UUID,
    val name: String,
    val imageUrl: String
)