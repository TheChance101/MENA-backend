package net.thechance.trends.api.controller

import jakarta.validation.Valid
import net.thechance.trends.api.dto.base.PagingResponse
import net.thechance.trends.api.dto.reel.*
import net.thechance.trends.service.ReelsService
import org.springframework.data.domain.Pageable
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.util.*

@RestController
@RequestMapping("/trends/reels")
class ReelsController(
    private val reelsService: ReelsService
) {

    @GetMapping("/feed", "/feed/{reelId}")
    fun getAllReelsForFeed(
        pageable: Pageable,
        @PathVariable(required = false) reelId: UUID? = null,
        @AuthenticationPrincipal currentUserId: UUID
    ): ResponseEntity<PagingResponse<ReelResponse>> {
        val reels = reelsService.getAllReelsForFeed(pageable, currentUserId, reelId).content.map { reel ->
            reel.toResponse().withOwnership(
                currentUserId = currentUserId,
                ownerId = reel.getReel().ownerId,
            )
        }

        val result = PagingResponse(
            pageNumber = pageable.pageNumber,
            results = reels,
            totalResults = reels.size
        )

        return ResponseEntity.ok(result)
    }

    @DeleteMapping("/{id}")
    fun deleteReelById(
        @PathVariable id: UUID,
        @AuthenticationPrincipal currentUserId: UUID
    ): ResponseEntity<Unit> {

        reelsService.deleteReelById(id, currentUserId)
        return ResponseEntity.noContent().build()
    }

    @PutMapping("/{id}")
    fun updateReelById(
        @PathVariable id: UUID,
        @AuthenticationPrincipal currentUserId: UUID,
        @Valid @RequestBody updateReelRequest: UpdateReelRequest
    ): ResponseEntity<ReelResponse> {
        val updatedReel = reelsService.updateReelDescriptionAndCategories(
            reelId = id,
            ownerId = currentUserId,
            newDescription = updateReelRequest.description,
            categoryIds = updateReelRequest.categoryIds
        )

        return ResponseEntity.ok(updatedReel.toResponse())
    }

    @PostMapping
    fun uploadReel(
        @AuthenticationPrincipal currentUserId: UUID,
        @RequestParam("video") video: MultipartFile,
    ): ResponseEntity<UploadReelResponse> {
        val reelId = reelsService.uploadReel(currentUserId, video)
        return ResponseEntity.ok(UploadReelResponse(reelId = reelId))
    }

    @PutMapping("/{reelId}/thumbnail")
    fun uploadThumbnail(
        @PathVariable reelId: UUID,
        @AuthenticationPrincipal currentUserId: UUID,
        @RequestParam("thumbnail") thumbnail: MultipartFile
    ): ResponseEntity<ReelResponse> {
        val updatedReel = reelsService.uploadThumbnail(
            reelId = reelId,
            ownerId = currentUserId,
            thumbnailFile = thumbnail
        )

        return ResponseEntity.ok(updatedReel.toResponse())
    }

    @PostMapping("/{reelId}/view")
    fun recordView(
        @PathVariable reelId: UUID,
        @AuthenticationPrincipal currentUserId: UUID
    ): ResponseEntity<Unit> {
        reelsService.incrementViewCount(reelId, currentUserId)
        return ResponseEntity.ok().build()
    }

    @PostMapping("/{reelId}/like")
    fun likeReel(
        @PathVariable reelId: UUID,
        @AuthenticationPrincipal currentUserId: UUID
    ): ResponseEntity<ReelResponse> {
        val reel = reelsService.likeReel(reelId = reelId, currentUserId)

        val reelResponse = reel.toResponse().withOwnership(currentUserId = currentUserId, ownerId = reel.getReel().ownerId)
        return ResponseEntity.ok(reelResponse)
    }

    @DeleteMapping("/{reelId}/like")
    fun removeReelLike(
        @PathVariable reelId: UUID,
        @AuthenticationPrincipal currentUserId: UUID
    ): ResponseEntity<ReelResponse>{
        val reel = reelsService.unlikeReel(reelId, currentUserId)

        val reelResponse = reel.toResponse().withOwnership(currentUserId = currentUserId, ownerId = reel.getReel().ownerId)
        return ResponseEntity.ok(reelResponse)
    }
}