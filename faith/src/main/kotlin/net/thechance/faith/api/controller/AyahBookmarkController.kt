package net.thechance.faith.api.controller

import jakarta.validation.Valid
import net.thechance.faith.api.dto.bookmark.AyahBookmarkRequest
import net.thechance.faith.api.dto.bookmark.AyahBookmarkResponse
import net.thechance.faith.api.dto.bookmark.toBookmark
import net.thechance.faith.api.dto.bookmark.toBookmarkResponse
import net.thechance.faith.service.AyahBookmarkService
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.domain.Sort.Direction
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("faith/ayah/bookmark")
class AyahBookmarkController(
    private val ayahBookmarkService: AyahBookmarkService
) {

    @PostMapping
    fun saveAyahBookmark(
        @Valid @RequestBody ayahBookmarkRequest: AyahBookmarkRequest,
        @AuthenticationPrincipal userId: UUID
    ): ResponseEntity<AyahBookmarkResponse> {
        val savedBookmark = ayahBookmarkService.saveBookmark(ayahBookmarkRequest.toBookmark(userId))
        val response = savedBookmark.toBookmarkResponse()
        return ResponseEntity.ok(response)
    }

    @DeleteMapping("/{id}")
    fun delete(
        @PathVariable id: Int,
        @AuthenticationPrincipal userId: UUID
    ): ResponseEntity<Void> {
        ayahBookmarkService.deleteByIdAndUserId(id, userId)
        return ResponseEntity.ok().build()
    }

    @GetMapping("/all")
    fun getAyahBookmarks(
        @AuthenticationPrincipal userId: UUID,
        @RequestParam(defaultValue = "1") page: Int,
        @RequestParam(defaultValue = "10") size: Int
    ): ResponseEntity<Page<AyahBookmarkResponse>> {

        val zeroBasedPage = if (page > 0) page - 1 else 0

        val pageable: Pageable = PageRequest.of(
            zeroBasedPage,
            size,
            Sort.by(Direction.DESC, "createdAt")
        )

        val bookmarks = ayahBookmarkService.getBookmarks(userId = userId, pageable = pageable)
        val response = bookmarks.map { it.toBookmarkResponse() }
        return ResponseEntity.ok(response)
    }
}
