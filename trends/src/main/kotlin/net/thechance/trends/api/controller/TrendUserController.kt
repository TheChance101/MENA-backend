package net.thechance.trends.api.controller

import net.thechance.trends.api.dto.analytics.SubmitWatchTimeRequest
import net.thechance.trends.api.dto.base.PagingResponse
import net.thechance.trends.api.dto.trend.TrendResponse
import net.thechance.trends.api.dto.trend.toResponse
import net.thechance.trends.service.TrendUserService
import net.thechance.trends.service.TrendsService
import org.springframework.data.domain.Pageable
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/trends/user")
class TrendUserController(
    private val trendsService: TrendsService,
    private val trendUserService: TrendUserService,
) {

    @GetMapping("/{trendId}", "")
    fun getAllTrendsByUserId(
        pageable: Pageable,
        @PathVariable(required = false) trendId: UUID? = null,
        @AuthenticationPrincipal currentUserId: UUID
    ): ResponseEntity<PagingResponse<TrendResponse>> {

        val trends = trendsService.getAllTrendsByUserId(pageable, currentUserId, trendId).content.map { it.toResponse() }

        val result = PagingResponse(
            pageNumber = pageable.pageNumber,
            results = trends,
            totalResults = trends.size
        )

        return ResponseEntity.ok(result)
    }

    @PostMapping("watch-time")
    fun submitUserWatchTime(
        @RequestBody submitWatchTimeRequest: SubmitWatchTimeRequest,
        @AuthenticationPrincipal currentUserId: UUID,
    ){
        trendUserService.updateUserAffinities(userId = currentUserId, submitWatchTimeRequest)
    }
}