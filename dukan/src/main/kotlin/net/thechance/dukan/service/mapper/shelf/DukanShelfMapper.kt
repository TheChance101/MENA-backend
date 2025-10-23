package net.thechance.dukan.service.mapper.shelf

import net.thechance.dukan.api.dto.shelf.DukanShelfResponse
import net.thechance.dukan.entity.DukanShelf

fun DukanShelf.toResponse(): DukanShelfResponse {
    return DukanShelfResponse(
        id = this.id,
        title = this.title,
    )
}