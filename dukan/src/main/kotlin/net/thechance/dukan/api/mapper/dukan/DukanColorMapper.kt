package net.thechance.dukan.api.mapper.dukan

import net.thechance.dukan.api.dto.color.DukanColorDto
import net.thechance.dukan.entity.DukanColor

fun DukanColor.toDto(): DukanColorDto {
    return DukanColorDto(id.toString(), hexCode)
}