package net.thechance.dukan.api.controller

import net.thechance.dukan.api.dto.DukanCategoryResponse
import net.thechance.dukan.mapper.DukanLanguage
import net.thechance.dukan.mapper.toDto
import net.thechance.dukan.service.DukanService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/dukan")
class DukanController(
    private val dukanService: DukanService
) {
    @GetMapping("/styles")
    fun getAllStyles() = dukanService.getAllStyles()

    @GetMapping("/categories")
    fun getAllCategories(): ResponseEntity<DukanCategoryResponse> {
        val categories = dukanService.getAllCategories().let { categories ->
            // Todo replace default Arabic with the extracted language from the header
            categories.map { category -> category.toDto(DukanLanguage.ARABIC) }
        }
        return ResponseEntity.ok(DukanCategoryResponse(categories))
    }
}