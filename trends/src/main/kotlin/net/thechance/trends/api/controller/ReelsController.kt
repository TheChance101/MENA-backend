package net.thechance.trends.api.controller

import jakarta.validation.Valid
import net.thechance.trends.api.dto.PagingResponse
import net.thechance.trends.api.dto.reel.ReelResponse
import net.thechance.trends.api.dto.reel.UpdateReelRequest
import net.thechance.trends.api.dto.reel.UploadReelResponse
import net.thechance.trends.api.dto.reel.toResponse
import net.thechance.trends.service.ReelsService
import org.springframework.data.domain.Pageable
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import java.util.UUID

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

        val reels = reelsService.getAllReelsByUserId(pageable, currentUserId).content.toResponse()

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
        @RequestParam video: MultipartFile,
    ): ResponseEntity<UploadReelResponse> {
        val reelId = reelsService.uploadReel(currentUserId, video)
        return ResponseEntity.ok(UploadReelResponse(reelId = reelId))
    }

    @PostMapping("/{id}/thumbnail")
    fun uploadThumbnail(
        @PathVariable id: UUID,
        @AuthenticationPrincipal currentUserId: UUID,
        @RequestParam thumbnail: MultipartFile
    ): ResponseEntity<ReelResponse> {
        val updatedReel = reelsService.uploadThumbnail(
            reelId = id,
            ownerId = currentUserId,
            thumbnailFile = thumbnail
        )

        return ResponseEntity.ok(updatedReel.toResponse())
    }

    @PostMapping
    fun uploadReel(
        @AuthenticationPrincipal currentUserId: UUID,
        @RequestParam video: MultipartFile,
    ): ResponseEntity<UploadReelResponse> {
        val reelId = reelsService.uploadReel(currentUserId, video)
        return ResponseEntity.ok(UploadReelResponse(reelId = reelId))
    }
}