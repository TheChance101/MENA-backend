package net.thechance.faith.api.dto.bookmark

data class BookmarkResponse(
    val id: String,
    val surahId: Int,
    val ayahNumber: Int,
    val createdAt: String
)
