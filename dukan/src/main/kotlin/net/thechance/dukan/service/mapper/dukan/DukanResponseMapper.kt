package net.thechance.dukan.service.mapper.dukan

import net.thechance.dukan.api.dto.dukan.DukanResponse
import net.thechance.dukan.entity.Dukan

fun Dukan.toDukanResponse(): DukanResponse {
    return DukanResponse(
        id =  id,
        name = name,
        imageUrl = imageUrl.orEmpty()
    )
}