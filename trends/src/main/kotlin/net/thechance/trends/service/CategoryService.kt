package net.thechance.trends.service

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
    fun getCategoriesToBeSaved(categoryIds: List<UUID>): List<Category> {
        if (categoryIds.isEmpty()) {
            throw IllegalArgumentException("Category IDs cannot be empty")
        }

        val categories = categoryRepository.findAllById(categoryIds) ?: throw NoSuchElementException("No Categories found for given Ids")
        if(categories.size != categoryIds.size) {
            val foundIds = categories.map { it.id }.toSet()
            val missingIds = categoryIds.filterNot { it in foundIds }
            throw IllegalArgumentException("Invalid category IDs: $missingIds")
        }
        return categories
    }
}