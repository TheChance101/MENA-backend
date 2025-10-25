package net.thechance.dukan.api.controller

import jakarta.validation.Valid
import net.thechance.dukan.api.utils.EndPoints.DUKAN_PATH
import net.thechance.dukan.api.dto.shelf.DukanShelfCreationRequest
import net.thechance.dukan.api.dto.shelf.DukanShelfResponse
import net.thechance.dukan.entity.DukanShelf
import net.thechance.dukan.service.DukanShelfService
import net.thechance.dukan.api.mapper.shelf.toResponse
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("${DUKAN_PATH}/shelf")
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

    @GetMapping("/{dukanId}")
    fun getAllShelvesByDukanId(
        @PathVariable dukanId: UUID,
        @PageableDefault(size = 10, page = 0)
        pageable: Pageable
    ): ResponseEntity<Page<DukanShelfResponse>> {
        val shelvesPage = dukanShelfService.getAllShelvesByDukanId(dukanId, pageable)
            .map(DukanShelf::toResponse)
        return ResponseEntity.ok(shelvesPage)
    }
}