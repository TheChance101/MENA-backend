package net.thechance.trends.api.controller

import net.thechance.trends.api.dto.trendUser.DoesUserHaveCategoriesResponse
import net.thechance.trends.service.TrendUserService
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/${Constants.TRENDS_PATH}")
class TrendUserController(
    private val trendUserService: TrendUserService
) {

    @GetMapping("/user/categories/status")
    fun getDoesUserHaveCategories(
        @AuthenticationPrincipal userId: UUID,
    ): ResponseEntity<DoesUserHaveCategoriesResponse> {
        val doesUserHaveCategories = trendUserService.getDoesUserHaveCategories(userId)
        val response = DoesUserHaveCategoriesResponse(hasCategory = doesUserHaveCategories)
        return ResponseEntity.ok(response)
    }
}