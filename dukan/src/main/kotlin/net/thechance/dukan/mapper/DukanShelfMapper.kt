package net.thechance.dukan.mapper

import net.thechance.dukan.api.dto.DukanShelfResponse
import net.thechance.dukan.entity.DukanShelf

fun DukanShelf.toResponse(): DukanShelfResponse {
    return DukanShelfRe ssponse(
        id = this.id,
        title = this.title,
    )
}