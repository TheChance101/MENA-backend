package net.thechance.trends.api.controller.category

import net.thechance.trends.api.controller.Constants
import net.thechance.trends.api.dto.category.GetAllCategoriesResponse
import net.thechance.trends.api.dto.category.toCategoryResponse
import jakarta.validation.Valid
import net.thechance.trends.api.dto.category.SubmitUserCategoriesRequest
import net.thechance.trends.api.dto.category.SubmitUserCategoriesResponse
import net.thechance.trends.service.CategoryService
import net.thechance.trends.service.TrendUserService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

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
        val categories = categoryService.getCategoriesToBeSaved(userCategoriesRequest.categoryIds)
        trendUserService.saveCategoriesToUser(userId, categories)

        return ResponseEntity.ok(
            SubmitUserCategoriesResponse(
                categoryIds = userCategoriesRequest.categoryIds,
            )
        )
    }

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