package net.thechance.dukan.api.controller

import net.thechance.dukan.api.dto.DukanStyleResponse
import net.thechance.dukan.mapper.toDukanStyleResponse
import net.thechance.dukan.api.dto.DukanCategoryResponse
import net.thechance.dukan.mapper.DukanLanguage
import jakarta.validation.constraints.NotBlank
import net.thechance.dukan.api.dto.DukanColorResponse
import net.thechance.dukan.api.dto.DukanNameAvailabilityResponse
import net.thechance.dukan.mapper.toDto
import net.thechance.dukan.api.dto.DukanStatuesResponse
import net.thechance.dukan.service.DukanService
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

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