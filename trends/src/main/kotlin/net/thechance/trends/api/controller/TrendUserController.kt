package net.thechance.trends.api.controller

import net.thechance.trends.api.dto.base.PagingResponse
import net.thechance.trends.api.dto.reel.ReelResponse
import net.thechance.trends.api.dto.reel.toResponse
import net.thechance.trends.api.dto.reel.withOwnership
import net.thechance.trends.service.ReelsService
import net.thechance.trends.service.TrendUserService
import org.springframework.data.domain.Pageable
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*

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
                ownerId = reel.getReel().ownerId
            )
        }

        val result = PagingResponse(
            pageNumber = pageable.pageNumber,
            results = reels,
            totalResults = reels.size
        )

        return ResponseEntity.ok(result)
    }
}