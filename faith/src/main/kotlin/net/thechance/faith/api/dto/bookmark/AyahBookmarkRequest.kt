package net.thechance.faith.api.dto.bookmark

import jakarta.validation.constraints.Min

data class AyahBookmarkRequest(
    @field:Min(value = 1, message = "Surah ID must be at least 1")
    val surahId: Int,

    @field:Min(value = 1, message = "Ayah number must be at least 1")
    val ayahNumber: Int
)
