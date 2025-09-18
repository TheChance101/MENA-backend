package net.thechance.trends.api.controller.category

import net.thechance.trends.api.controller.Constants
import net.thechance.trends.api.dto.category.GetAllCategoriesResponse
import net.thechance.trends.api.dto.category.toCategoryResponse
import net.thechance.trends.service.CategoryService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/${Constants.TRENDS_PATH}/category")
class CategoryController(
    private val categoryService: CategoryService
) {

    @GetMapping
    fun getAllCategories(): ResponseEntity<GetAllCategoriesResponse> {
        val categories = categoryService.getAllCategories()
        val response = GetAllCategoriesResponse(
            message = "Success",
            data = categories.map { it.toCategoryResponse() }
        )
        return ResponseEntity.ok(response)
    }
}