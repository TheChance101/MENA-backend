package net.thechance.dukan.api.mapper.category

import net.thechance.dukan.api.dto.category.DukanCategoryDto
import net.thechance.dukan.entity.DukanCategory

fun DukanCategory.toDto(language: String): DukanCategoryDto {
    return DukanCategoryDto(
        id = id.toString(),
        icon = iconUrl,
        title = if (language == "ar") arabicTitle else englishTitle
    )
}