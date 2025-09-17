package net.thechance.trends.api.controller

import jakarta.validation.Valid
import net.thechance.trends.api.dto.category.SubmitUserCategoriesRequest
import net.thechance.trends.api.dto.category.SubmitUserCategoriesResponse
import net.thechance.trends.service.CategoryService
import net.thechance.trends.service.TrendUserService
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/trends")
class CategoryController(
    private val categoryService: CategoryService,
    private val trendUserService: TrendUserService,
) {

    @PostMapping("/interests")
    fun postUserInterests(
        @RequestBody @Valid userCategoriesRequest: SubmitUserCategoriesRequest,
        @AuthenticationPrincipal userId: UUID
    ): ResponseEntity<SubmitUserCategoriesResponse> {
        val categories = categoryService.getCategoriesToBeSaved(userCategoriesRequest.categoryIds)
        trendUserService.saveCategoriesToUser(userId, categories)

        return ResponseEntity.ok(
            SubmitUserCategoriesResponse(
                userId = userId,
                categoryIds = userCategoriesRequest.categoryIds,
                message = "User interests updated successfully"
            )
        )
    }
}