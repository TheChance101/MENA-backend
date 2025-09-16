package net.thechance.dukan.mapper

import net.thechance.dukan.api.dto.CategoryDto
import net.thechance.dukan.entity.DukanCategory

fun DukanCategory.toDto(): CategoryDto {
    return CategoryDto(
        id = id.toString(),
        icon = iconUrl,
        arabicTitle = arabicTitle,
        englishTitle = englishTitle,
    )
}