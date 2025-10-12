package net.thechance.dukan.mapper

import net.thechance.dukan.api.dto.DukanResponse
import net.thechance.dukan.entity.Dukan

fun Dukan.toDukanResponse(): DukanResponse {
    return DukanResponse(
        id =  id,
        name = name,
        imageUrl = imageUrl.orEmpty()
    )
}