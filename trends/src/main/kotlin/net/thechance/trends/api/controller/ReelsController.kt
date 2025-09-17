package net.thechance.trends.api.controller

import net.thechance.trends.service.ReelsService
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
@RequestMapping("/trends/reels")
class ReelsController(
    private val reelsService: ReelsService
) {

    @DeleteMapping("/{id}")
    fun deleteReelById(
        @PathVariable id: UUID,
        @AuthenticationPrincipal currentUserId: UUID
    ): ResponseEntity<Unit> {

        reelsService.deleteReelById(id, currentUserId)
        return ResponseEntity.noContent().build()
    }
}