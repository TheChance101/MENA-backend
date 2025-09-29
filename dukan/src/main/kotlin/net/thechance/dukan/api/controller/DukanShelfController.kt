package net.thechance.dukan.api.controller

import jakarta.validation.Valid
import net.thechance.dukan.api.dto.DukanShelfCreationRequest
import net.thechance.dukan.api.dto.DukanShelfResponse
import net.thechance.dukan.entity.DukanShelf
import net.thechance.dukan.mapper.toResponse
import net.thechance.dukan.service.DukanShelfService
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/dukan/shelf")
class DukanShelfController(
    val dukanShelfService: DukanShelfService
) {

    @PostMapping("/create")
    fun createShelf(
        @Valid @RequestBody requestBody: DukanShelfCreationRequest,
        @AuthenticationPrincipal userId: UUID,
    ): ResponseEntity<Unit> {
        dukanShelfService.createShelf(
            ownerId = userId,
            title = requestBody.title,
        )
        return ResponseEntity.ok().build()
    }

    @GetMapping
    fun getMyDukanShelves(
        @AuthenticationPrincipal userId: UUID,
    ): ResponseEntity<List<DukanShelfResponse>> {
        val shelves = dukanShelfService
            .getDukanShelvesByOwnerId(userId)
            .map(DukanShelf::toResponse)
        return ResponseEntity.ok(shelves)
    }

    @DeleteMapping("/{shelfId}")
    fun deleteShelf(
        @PathVariable shelfId: UUID,
        @AuthenticationPrincipal userId: UUID,
    ): ResponseEntity<Unit> {
        dukanShelfService.deleteShelf(shelfId, userId)
        return ResponseEntity.noContent().build()
    }
}