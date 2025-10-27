package net.thechance.dukan.api.dto.shelf

import java.util.UUID

data class DukanShelfResponse(
    val id: UUID,
    val title: String,
)