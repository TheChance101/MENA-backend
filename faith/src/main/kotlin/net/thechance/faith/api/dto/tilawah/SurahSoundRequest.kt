package net.thechance.faith.api.dto.tilawah

import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min

data class SurahSoundRequest(
    val reciterId: Int,

    @field:Min(value = 1, message = "Surah number must be at least 1")
    @field:Max(value = 114, message = "Surah number must be at max 114")
    val surahNumber: Int,
)
