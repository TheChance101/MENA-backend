package net.thechance.trends.service

import net.thechance.trends.entity.Category
import net.thechance.trends.repository.CategoryRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class CategoryService(
    private val categoryRepository: CategoryRepository
) {

    fun getAllCategories(): List<Category> {
        return categoryRepository.findAll()
    }
}