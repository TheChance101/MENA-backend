package net.thechance.faith.api.dto.bookmark

import net.thechance.faith.entity.AyahBookmark
import java.util.*

fun BookmarkRequest.toBookmark(userId: UUID): AyahBookmark {
    return AyahBookmark(
        userId = userId,
        surahId = surahId,
        ayahNumber = ayahNumber
    )
}

fun AyahBookmark.toBookmarkResponse(): BookmarkResponse {
    return BookmarkResponse(
        id = id.toString(),
        surahId = surahId,
        ayahNumber = ayahNumber,
        createdAt = createdAt.toString()
    )
}