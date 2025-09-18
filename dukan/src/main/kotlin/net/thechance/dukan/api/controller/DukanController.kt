package net.thechance.dukan.api.controller

import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import net.thechance.dukan.api.dto.DukanCreationRequest
import net.thechance.dukan.api.dto.DukanNameAvailabilityResponse
import net.thechance.dukan.entity.Dukan
import net.thechance.dukan.mapper.toDukanCreationParams
import net.thechance.dukan.service.DukanService
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.util.*


@RestController
@RequestMapping("/dukan")
class DukanController(
    private val dukanService: DukanService,
) {

    @GetMapping("/available")
    fun checkNameAvailability(
        @RequestParam(name = "name") @NotBlank name: String
    ): ResponseEntity<DukanNameAvailabilityResponse> {
        val available = dukanService.isDukanNameAvailable(name)
        return ResponseEntity.ok(DukanNameAvailabilityResponse(available))
    }

    @PostMapping("/create")
    fun createDukan(
        @Valid @RequestBody requestBody: DukanCreationRequest,
        @AuthenticationPrincipal userId: UUID,
    ): ResponseEntity<Dukan> {
        val dukan = dukanService.createDukan(requestBody.toDukanCreationParams(userId))
        return ResponseEntity.ok(dukan)
    }

    @PostMapping("/image")
    fun uploadDukanImage(
        @AuthenticationPrincipal ownerId: UUID,
        @RequestParam("file") file: MultipartFile,
    ): ResponseEntity<String> {
        val imageUrl = dukanService.uploadDukanImage(ownerId, file)
        return ResponseEntity.ok(imageUrl)
    }
}