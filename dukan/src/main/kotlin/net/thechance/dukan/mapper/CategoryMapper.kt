package net.thechance.dukan.mapper

import net.thechance.dukan.api.dto.DukanCategoryDto
import net.thechance.dukan.entity.DukanCategory

fun DukanCategory.toDto(language: DukanLanguage): DukanCategoryDto {
    return DukanCategoryDto(
        id = id.toString(),
        icon = iconUrl,
        title = if (language == DukanLanguage.ARABIC) arabicTitle else englishTitle
    )
}

enum class DukanLanguage {
    ARABIC, ENGLISH
}