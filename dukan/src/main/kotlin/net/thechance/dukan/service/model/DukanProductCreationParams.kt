package net.thechance.dukan.service.model

import net.thechance.dukan.entity.Price
import java.util.UUID

data class DukanProductCreationParams(
    val name: String,
    val ownerId: UUID,
    val shelfId: UUID,
    val description: String,
    val price: Price
)
