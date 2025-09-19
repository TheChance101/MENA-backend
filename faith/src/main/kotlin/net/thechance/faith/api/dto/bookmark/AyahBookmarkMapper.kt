package net.thechance.faith.api.dto.bookmark

import net.thechance.faith.entity.AyahBookmark
import java.util.*

fun AyahBookmarkRequest.toBookmark(userId: UUID): AyahBookmark {
    return AyahBookmark(
        userId = userId,
        surahId = surahId,
        ayahNumber = ayahNumber
    )
}

fun AyahBookmark.toBookmarkResponse(): AyahBookmarkResponse {
    return AyahBookmarkResponse(
        id = id.toString(),
        surahId = surahId,
        ayahNumber = ayahNumber,
        createdAt = createdAt.toString()
    )
}