package net.thechance.faith.api.controller

import jakarta.validation.Valid
import net.thechance.faith.api.dto.bookmark.BookmarkRequest
import net.thechance.faith.api.dto.bookmark.BookmarkResponse
import net.thechance.faith.api.dto.bookmark.toBookmark
import net.thechance.faith.api.dto.bookmark.toBookmarkResponse
import net.thechance.faith.service.BookmarkService
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
@RequestMapping("/bookmark")
class BookmarkController(
    private val bookmarkService: BookmarkService
) {
    @PostMapping("/save")
    fun saveBookmark(
        @Valid @RequestBody bookmarkRequest: BookmarkRequest,
        @AuthenticationPrincipal userId: UUID
    ): ResponseEntity<BookmarkResponse> {
        val savedBookmark = bookmarkService.saveBookmark(bookmarkRequest.toBookmark(userId))
        val response = savedBookmark.toBookmarkResponse()
        return ResponseEntity.ok(response)
    }
}
