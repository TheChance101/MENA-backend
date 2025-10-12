package net.thechance.dukan.mapper

import net.thechance.dukan.api.dto.DukanProductCreationRequest
import net.thechance.dukan.service.model.DukanProductCreationParams
import java.util.UUID

fun DukanProductCreationRequest.toProductCreationParams(ownerId: UUID) = DukanProductCreationParams(
    name = name,
    ownerId = ownerId,
    shelfId = shelfId,
    description = description,
    price = price,
)