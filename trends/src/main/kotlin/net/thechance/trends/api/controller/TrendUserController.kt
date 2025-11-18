package net.thechance.trends.api.controller

import net.thechance.trends.api.dto.base.PagingResponse
import net.thechance.trends.api.dto.trend.TrendResponse
import net.thechance.trends.api.dto.trend.toResponse
import net.thechance.trends.service.TrendsService
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.domain.Pageable
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
@RequestMapping("/trends/user")
class TrendUserController(
    @Value("\${storage.mena.cdn-endpoint}") cdnEndpoint: String,
    @Value("\${identity.resources.profile-image-directory}") profileImageDirectory: String,
    private val trendsService: TrendsService
) {

    private val imagesBaseUrl: String = "$cdnEndpoint$profileImageDirectory"

    @GetMapping("/{trendId}", "")
    fun getAllTrendsByUserId(
        pageable: Pageable,
        @PathVariable(required = false) trendId: UUID? = null,
        @AuthenticationPrincipal currentUserId: UUID
    ): ResponseEntity<PagingResponse<TrendResponse>> {

        val trends = trendsService.getAllTrendsByUserId(pageable, currentUserId, trendId).content.map { it.toResponse(imagesBaseUrl) }

        val result = PagingResponse(
            pageNumber = pageable.pageNumber,
            results = trends,
            totalResults = trends.size
        )

        return ResponseEntity.ok(result)
    }
}