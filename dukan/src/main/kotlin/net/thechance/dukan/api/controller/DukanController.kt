package net.thechance.dukan.api.controller

import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import net.thechance.dukan.api.dto.*
import net.thechance.dukan.entity.Dukan
import net.thechance.dukan.mapper.DukanLanguage
import net.thechance.dukan.mapper.toDto
import net.thechance.dukan.mapper.toDukanCreationParams
import net.thechance.dukan.mapper.toDukanStyleResponse
import net.thechance.dukan.service.DukanService
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.util.*


@RestController
@RequestMapping("/dukan")
class DukanController(
    private val dukanService: DukanService
) {
    @GetMapping("/styles")
    fun getAllStyles(): ResponseEntity<DukanStyleResponse> {
        val styles = dukanService.getAllStyles().toDukanStyleResponse()
       return ResponseEntity.ok(styles)
    }

    @GetMapping("/categories")
    fun getAllCategories(): ResponseEntity<DukanCategoryResponse> {
        val categories = dukanService.getAllCategories().let { categories ->
            // Todo replace default Arabic with the extracted language from the header
            categories.map { category -> category.toDto(DukanLanguage.ARABIC) }
        }
        return ResponseEntity.ok(DukanCategoryResponse(categories))
    }

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

    @GetMapping("/colors")
    fun getAllColors(): ResponseEntity<DukanColorResponse> {
        val colors = dukanService.getAllColors().map { it.toDto() }
        return ResponseEntity.ok(DukanColorResponse(colors))
    }
    
    @GetMapping("/statues")
    fun getDukanStatues(
        @AuthenticationPrincipal userId: UUID,
    ): ResponseEntity<DukanStatuesResponse> {
        val dukan = dukanService.getDukanByOwnerId(userId)
        return ResponseEntity.ok(DukanStatuesResponse(dukan.name, dukan.status))
    }
}