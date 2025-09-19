package net.thechance.faith.api.dto.bookmark

data class AyahBookmarkResponse(
    val id: String,
    val surahId: Int,
    val ayahNumber: Int,
    val createdAt: String
)
