package net.thechance.faith.api.dto.bookmark

import net.thechance.faith.entity.Bookmark
import java.util.*

fun BookmarkRequest.toBookmark(userId: UUID): Bookmark {
    return Bookmark(
        userId = userId,
        surahId = surahId,
        ayahNumber = ayahNumber
    )
}

fun Bookmark.toBookmarkResponse(): BookmarkResponse {
    return BookmarkResponse(
        id = id.toString(),
        surahId = surahId,
        ayahNumber = ayahNumber,
        createdAt = createdAt.toString()
    )
}