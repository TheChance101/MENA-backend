package net.thechance.trends.api.controller

import jakarta.validation.Valid
import net.thechance.trends.api.dto.base.PagingResponse
import net.thechance.trends.api.dto.trend.*
import net.thechance.trends.service.TrendsService
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.domain.Pageable
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.util.*

@RestController
@RequestMapping("/trends")
class TrendsController(
    @Value("\${storage.mena.cdn-endpoint}") cdnEndpoint: String,
    @Value("\${identity.resources.profile-image-directory}") profileImageDirectory: String,
    private val trendsService: TrendsService
) {

    private val imagesBaseUrl: String = "$cdnEndpoint$profileImageDirectory"

    @GetMapping("/{trendId}/refresh")
    fun getRefreshedTrendUrls(
        @PathVariable trendId: UUID,
    ): ResponseEntity<TrendPathsResponse> {
        val signedUrls = trendsService.generatePresignedUrlsForTrend(trendId)
        val trendPaths = TrendPathsResponse(signedUrls.videoUrl, signedUrls.thumbnailUrl)

        return ResponseEntity.ok(trendPaths)
    }

    @GetMapping("/feed", "/feed/{trendsId}")
    fun getAllTrendsForFeed(
        pageable: Pageable,
        @PathVariable(required = false) trendsId: UUID? = null,
        @AuthenticationPrincipal currentUserId: UUID
    ): ResponseEntity<PagingResponse<TrendResponse>> {
        val trends = trendsService.getAllTrendsForFeed(
            pageable,
            currentUserId,
            trendsId
        ).content.map { trend -> trend.toResponse(imagesBaseUrl) }

        val result = PagingResponse(
            pageNumber = pageable.pageNumber,
            results = trends,
            totalResults = trends.size
        )

        return ResponseEntity.ok(result)
    }

    @GetMapping("/favorites", "/favorites/{trendsId}")
    fun getFavoriteTrends(
        pageable: Pageable,
        @PathVariable(required = false) trendsId: UUID? = null,
        @AuthenticationPrincipal currentUserId: UUID
    ): ResponseEntity<PagingResponse<TrendResponse>> {
        val trends = trendsService.getUserFavoriteTrends(
            pageable = pageable,
            currentUserId = currentUserId,
            trendId = trendsId,
        ).content.map { trend -> trend.toResponse(imagesBaseUrl) }

        val result = PagingResponse(
            pageNumber = pageable.pageNumber,
            results = trends,
            totalResults = trends.size
        )

        return ResponseEntity.ok(result)
    }

    @DeleteMapping("/{id}")
    fun deleteTrendById(
        @PathVariable id: UUID,
        @AuthenticationPrincipal currentUserId: UUID
    ): ResponseEntity<Unit> {

        trendsService.deleteTrendById(id, currentUserId)
        return ResponseEntity.noContent().build()
    }

    @PatchMapping("/{id}")
    fun updateTrendById(
        @PathVariable id: UUID,
        @AuthenticationPrincipal currentUserId: UUID,
        @Valid @RequestBody updateTrendRequest: UpdateTrendRequest
    ): ResponseEntity<TrendResponse> {
        val updatedTrend = trendsService.updateTrendDescriptionAndCategories(
            trendId = id,
            ownerId = currentUserId,
            newDescription = updateTrendRequest.description,
            categoryIds = updateTrendRequest.categoryIds
        )

        return ResponseEntity.ok(updatedTrend.toResponse())
    }

    @PostMapping
    fun uploadTrend(
        @AuthenticationPrincipal currentUserId: UUID,
        @RequestParam("video") video: MultipartFile,
    ): ResponseEntity<UploadTrendResponse> {
        val trendId = trendsService.uploadTrend(currentUserId, video)
        return ResponseEntity.ok(UploadTrendResponse(trendId = trendId))
    }

    @PatchMapping("/{trendId}/thumbnail")
    fun uploadThumbnail(
        @PathVariable trendId: UUID,
        @AuthenticationPrincipal currentUserId: UUID,
        @RequestParam("thumbnail") thumbnail: MultipartFile
    ): ResponseEntity<TrendResponse> {
        val updatedTrend = trendsService.uploadThumbnail(
            trendId = trendId,
            ownerId = currentUserId,
            thumbnailFile = thumbnail
        )

        return ResponseEntity.ok(updatedTrend.toResponse())
    }

    @PostMapping("/{trendId}/view")
    fun recordView(
        @PathVariable trendId: UUID,
        @AuthenticationPrincipal currentUserId: UUID
    ): ResponseEntity<Unit> {
        trendsService.incrementViewCount(trendId, currentUserId)
        return ResponseEntity.ok().build()
    }

    @PostMapping("/{trendId}/like")
    fun likeTrend(
        @PathVariable trendId: UUID,
        @AuthenticationPrincipal currentUserId: UUID
    ): ResponseEntity<TrendResponse> {
        val trend = trendsService.likeTrend(trendId = trendId, currentUserId)

        val trendResponse = trend.toResponse(imagesBaseUrl)
        return ResponseEntity.ok(trendResponse)
    }

    @DeleteMapping("/{trendId}/like")
    fun removeTrendLike(
        @PathVariable trendId: UUID,
        @AuthenticationPrincipal currentUserId: UUID
    ): ResponseEntity<TrendResponse> {
        val trend = trendsService.unlikeTrend(trendId, currentUserId)

        val trendResponse = trend.toResponse(imagesBaseUrl)
        return ResponseEntity.ok(trendResponse)
    }
}