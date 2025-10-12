package net.thechance.trends.api.controller

import jakarta.validation.Valid
import net.thechance.trends.api.dto.category.CategoryResponse
import net.thechance.trends.api.dto.category.SubmitUserCategoriesRequest
import net.thechance.trends.api.dto.category.SubmitUserCategoriesResponse
import net.thechance.trends.api.dto.category.toCategoryResponse
import net.thechance.trends.service.CategoryService
import net.thechance.trends.service.TrendUserService
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/${Constants.TRENDS_PATH}/categories")
class CategoryController(
    private val categoryService: CategoryService,
    private val trendUserService: TrendUserService,
) {

    @PostMapping
    fun addUserCategories(
        @RequestBody @Valid userCategoriesRequest: SubmitUserCategoriesRequest,
        @AuthenticationPrincipal userId: UUID
    ): ResponseEntity<SubmitUserCategoriesResponse> {
        trendUserService.saveCategoriesToUser(userId, userCategoriesRequest.categoryIds)

        return ResponseEntity.ok(
            SubmitUserCategoriesResponse(
                categoryIds = userCategoriesRequest.categoryIds,
            )
        )
    }

    @GetMapping
    fun getSelectedCategories(
        @AuthenticationPrincipal userId: UUID
    ): ResponseEntity<List<CategoryResponse>> {
        val allCategories = categoryService.getAllCategories()
        val userCategories = trendUserService.getUserSelectedCategories(userId)

        return ResponseEntity.ok(
            allCategories.map { category ->
                category.toCategoryResponse(isSelected = category in userCategories)
            }
        )
    }
}