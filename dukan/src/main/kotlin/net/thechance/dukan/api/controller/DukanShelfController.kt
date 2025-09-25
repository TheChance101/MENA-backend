package net.thechance.dukan.api.controller

import jakarta.validation.Valid
import net.thechance.dukan.api.dto.DukanShelfCreationRequest
import net.thechance.dukan.service.DukanShelfService
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
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

    @DeleteMapping("/{shelfId}")
    fun deleteShelf(
        @PathVariable shelfId: UUID,
        @AuthenticationPrincipal userId: UUID,
    ): ResponseEntity<Unit> {
        dukanShelfService.deleteShelf(shelfId, userId)
        return ResponseEntity.noContent().build()
    }
}