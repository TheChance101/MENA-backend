package net.thechance.faith.api.dto.bookmark

import jakarta.validation.constraints.Min
import org.hibernate.validator.constraints.Range

data class BookmarkRequest(
    @field:Min(value = 1, message = "Surah ID must be at least 1")
    val surahId: Int,

    @field:Range(min = 1, max = 286, message = "Ayah number must be between 1 and 286")
    val ayahNumber: Int
)
