package net.thechance.trends.api.controller

import net.thechance.trends.api.dto.PagingResponse
import net.thechance.trends.api.dto.ReelResponse
import net.thechance.trends.api.dto.toResponseList
import net.thechance.trends.service.ReelsService
import org.springframework.data.domain.Pageable
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
@RequestMapping("/trends/reels")
class ReelsController(
    private val reelsService: ReelsService
) {

    @GetMapping
    fun getAllReelsByUserId(
        pageable: Pageable,
        @AuthenticationPrincipal currentUserId: UUID
    ): ResponseEntity<PagingResponse<ReelResponse>> {

        val reels = reelsService.getAllReelsByUserId(pageable, currentUserId).content.toResponseList()

        val result = PagingResponse(
            pageNumber = pageable.pageNumber,
            results = reels,
            totalResults = reels.size
        )

        return ResponseEntity.ok(result)
    }
}