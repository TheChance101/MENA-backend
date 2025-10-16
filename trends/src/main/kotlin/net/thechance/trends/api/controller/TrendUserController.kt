package net.thechance.trends.api.controller

import net.thechance.trends.api.dto.PagingResponse
import net.thechance.trends.api.dto.reel.ReelResponse
import net.thechance.trends.api.dto.reel.toResponse
import net.thechance.trends.api.dto.reel.withOwnership
import net.thechance.trends.api.dto.trendUser.DoesUserHaveCategoriesResponse
import net.thechance.trends.service.ReelsService
import net.thechance.trends.service.TrendUserService
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/${Constants.TRENDS_PATH}/user")
class TrendUserController(
    private val trendUserService: TrendUserService,
    private val reelService: ReelsService
) {

    @GetMapping("/reels")
    fun getAllReelsByUserId(
        pageable: Pageable,
        @AuthenticationPrincipal currentUserId: UUID
    ): ResponseEntity<PagingResponse<ReelResponse>> {

        val reels = reelService.getAllReelsByUserId(pageable, currentUserId).content.map { reel ->
            reel.toResponse().withOwnership(
                currentUserId = currentUserId,
                ownerId = reel.ownerId
            )
        }

        val result = PagingResponse.create(
            pageNumber = pageable.pageNumber,
            results = reels,
            totalResults = reels.size
        )

        return ResponseEntity.ok(result)
    }

    @GetMapping("/categories/status")
    fun getDoesUserHaveCategories(
        @AuthenticationPrincipal userId: UUID,
    ): ResponseEntity<DoesUserHaveCategoriesResponse> {
        val doesUserHaveCategories = trendUserService.getDoesUserHaveCategories(userId)
        val response = DoesUserHaveCategoriesResponse(hasCategory = doesUserHaveCategories)
        return ResponseEntity.ok(response)
    }
}