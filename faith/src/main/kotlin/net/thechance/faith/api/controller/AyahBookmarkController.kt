package net.thechance.faith.api.controller

import jakarta.validation.Valid
import net.thechance.faith.api.dto.bookmark.AyahBookmarkRequest
import net.thechance.faith.api.dto.bookmark.AyahBookmarkResponse
import net.thechance.faith.api.dto.bookmark.toBookmark
import net.thechance.faith.api.dto.bookmark.toBookmarkResponse
import net.thechance.faith.service.AyahBookmarkService
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
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
}
