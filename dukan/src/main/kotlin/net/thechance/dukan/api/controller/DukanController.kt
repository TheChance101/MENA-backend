package net.thechance.dukan.api.controller

import net.thechance.dukan.api.dto.CategoryResponse
import net.thechance.dukan.mapper.DukanLanguage
import net.thechance.dukan.mapper.toCategoryResponse
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
    fun getAllCategories(): ResponseEntity<List<CategoryResponse>> {
        val categories = dukanService.getAllCategories().let { categories ->
            categories.map { category -> category.toCategoryResponse(DukanLanguage.ARABIC) }
        }
        return ResponseEntity.ok(categories)
    }
}