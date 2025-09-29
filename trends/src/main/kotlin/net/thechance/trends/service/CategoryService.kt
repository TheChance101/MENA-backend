package net.thechance.trends.service

import net.thechance.trends.api.controller.exception.InvalidTrendInputException
import net.thechance.trends.api.controller.exception.TrendCategoryNotFoundException
import net.thechance.trends.entity.Category
import net.thechance.trends.repository.CategoryRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
@Transactional(readOnly = true)
class CategoryService(
    private val categoryRepository: CategoryRepository
) {

    fun getAllCategories(): List<Category> {
        return categoryRepository.findAll()
    }
}