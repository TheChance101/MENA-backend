package net.thechance.dukan.mapper

import net.thechance.dukan.api.dto.CategoryDto
import net.thechance.dukan.api.dto.CategoryResponse
import net.thechance.dukan.entity.DukanCategory
import net.thechance.dukan.mapper.DukanLanguage

fun DukanCategory.toDto(language: DukanLanguage): CategoryDto {
    return CategoryDto(
        id = id.toString(),
        icon = iconUrl,
        title = if (language == DukanLanguage.ARABIC) arabicTitle else englishTitle
    )
}

enum class DukanLanguage {
    ARABIC, ENGLISH
}