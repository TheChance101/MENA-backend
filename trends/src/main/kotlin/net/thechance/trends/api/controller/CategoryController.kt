package net.thechance.trends.api.controller

import jakarta.validation.Valid
import net.thechance.trends.api.dto.category.*
import net.thechance.trends.service.TrendUserService
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/trends/categories")
class CategoryController(
    private val trendUserService: TrendUserService,
) {

    @PostMapping
    fun addUserCategories(
        @RequestBody @Valid userCategoriesRequest: SubmitUserCategoriesRequest,
        @AuthenticationPrincipal userId: UUID
    ): ResponseEntity<SubmitUserCategoriesResponse> {
        trendUserService.updateUserCategories(userId, userCategoriesRequest.categoryIds, emptyList())

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
        val userCategories = trendUserService.getUserSelectedCategories(userId)
        return ResponseEntity.ok(userCategories.map { it.toCategoryResponse() })
    }

    @PatchMapping
    fun patchUserCategories(
        @RequestBody @Valid patchRequest: PatchUserCategoriesRequest,
        @AuthenticationPrincipal userId: UUID
    ): ResponseEntity<PatchUserCategoriesResponse> {
        val patchMetadata = trendUserService.updateUserCategories(
            userId = userId,
            categoriesToAdd = patchRequest.add,
            categoriesToRemove = patchRequest.remove
        )

        val userCategories = trendUserService.getUserSelectedCategories(userId).map { category ->
            category.toCategoryResponse()
        }

        return ResponseEntity.ok(
            PatchUserCategoriesResponse(
                patchMetadata = patchMetadata,
                updatedCategories = userCategories
            )
        )
    }
}
