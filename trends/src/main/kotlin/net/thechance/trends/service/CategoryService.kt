package net.thechance.trends.service

import net.thechance.trends.api.controller.exception.InvalidTrendInputException
import net.thechance.trends.api.controller.exception.ReelNotFoundException
import net.thechance.trends.api.controller.exception.TrendCategoryNotFoundException
import net.thechance.trends.api.controller.exception.TrendExceptions
import net.thechance.trends.api.controller.exception.TrendUserNotFoundException
import net.thechance.trends.entity.Category
import net.thechance.trends.repository.CategoryRepository
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.ExceptionHandler
import java.util.UUID

@Service
@Transactional(readOnly = true)
class CategoryService(
    private val categoryRepository: CategoryRepository
) {
    fun getCategoriesToBeSaved(categoryIds: List<UUID>): List<Category> {
        if (categoryIds.isEmpty()) {
            throw InvalidTrendInputException()
        }

        val categories = categoryRepository.findAllById(categoryIds) ?: throw TrendCategoryNotFoundException()
        if(categories.size != categoryIds.size) {
            throw TrendCategoryNotFoundException()
        }
        return categories
    }
}