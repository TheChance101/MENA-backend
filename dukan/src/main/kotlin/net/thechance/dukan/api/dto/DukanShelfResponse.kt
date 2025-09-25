package net.thechance.dukan.api.dto

import java.util.UUID

data class DukanShelfResponse(
    val id: UUID,
    val title: String,
    val dukanId: UUID,
)