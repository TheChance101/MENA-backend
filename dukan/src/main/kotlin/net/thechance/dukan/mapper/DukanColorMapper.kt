package net.thechance.dukan.mapper

import net.thechance.dukan.api.dto.DukanColorDto
import net.thechance.dukan.entity.DukanColor

fun DukanColor.toDto(): DukanColorDto {
    return DukanColorDto(id.toString(), hexCode)
}