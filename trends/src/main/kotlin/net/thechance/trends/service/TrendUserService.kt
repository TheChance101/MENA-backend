package net.thechance.trends.service

import net.thechance.trends.api.dto.base.PatchMetadata
import net.thechance.trends.api.dto.category.toUserSelectedCategories
import net.thechance.trends.entity.UserCategories
import net.thechance.trends.exception.InvalidTrendInputException
import net.thechance.trends.exception.TrendCategoryNotFoundException
import net.thechance.trends.models.UserSelectedCategories
import net.thechance.trends.repository.CategoryRepository
import net.thechance.trends.repository.UserCategoryRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.*

@Service
@Transactional
class TrendUserService(
    private val categoryRepository: CategoryRepository,
    private val categoryService: CategoryService,
    private val userCategoryRepository: UserCategoryRepository
) {

    fun updateUserCategories(
        userId: UUID,
        categoriesToAdd: List<UUID>,
        categoriesToRemove: List<UUID>
    ): PatchMetadata {
        val allCategoryIds = (categoriesToAdd + categoriesToRemove).distinct()

        validateCategoriesNotEmpty(allCategoryIds)
        validateCategoriesExist(allCategoryIds)

        val currentCategories = userCategoryRepository.findUserCategoriesByUserId(userId)
        val currentCategoryIds = currentCategories.map { it.categoryId }

        val actualRemoved = categoriesToRemove.filterTo(mutableListOf()) { it in currentCategoryIds }

        val changedToSelected = categoriesToAdd.filterTo(mutableListOf()) { it in currentCategoryIds }

        val actualAdded = categoriesToAdd.filterTo(mutableListOf()) { it !in currentCategoryIds }

        if (actualAdded.isEmpty() && actualRemoved.isEmpty() && changedToSelected.isEmpty()) {
            return PatchMetadata(addedCount = 0, removedCount = 0)
        }

        val now = LocalDateTime.now()

        val updatedUserCategories = currentCategories.map { userCategory ->
            when (userCategory.categoryId) {
                in changedToSelected -> userCategory.copy(isSelected = true, lastUpdated = now)
                in actualRemoved -> userCategory.copy(isSelected = false, lastUpdated = now)
                else -> userCategory
            }
        }.plus (
            actualAdded.map { categoryId ->
                UserCategories(
                    userId = userId,
                    categoryId = categoryId,
                    isSelected = true,
                    lastUpdated = now
                )
            }
        )

        userCategoryRepository.saveAll(updatedUserCategories)

        return PatchMetadata(addedCount = actualAdded.size + changedToSelected.size, removedCount = actualRemoved.size)
    }

    fun getUserSelectedCategories(userId: UUID): List<UserSelectedCategories> {
        val allCategories = categoryService.getAllCategories()
        val userCategories = userCategoryRepository.findUserCategoriesByUserId(userId)

        val selectedCategoryIds = userCategories
            .filter { it.isSelected }
            .map { it.categoryId }
            .toSet()

        return allCategories.map { category ->
            category.toUserSelectedCategories(isSelected = category.id in selectedCategoryIds)
        }
    }

    fun updateUserAffinities(userId: UUID, categoryId: UUID) {
        val userCategory = userCategoryRepository.findByUserIdAndCategoryId(userId, categoryId)
        val affinity = userCategory?.affinity?.plus(1) ?: 0
        val newCategory = userCategory?.copy(affinity = affinity)
        if (newCategory != null) {
            userCategoryRepository.save(newCategory)
        }
    }

    private fun validateCategoriesNotEmpty(categoryIds: List<UUID>) {
        if (categoryIds.isEmpty()) throw InvalidTrendInputException()
    }

    private fun validateCategoriesExist(categoryIds: List<UUID>) {
        val existingCount = categoryRepository.countByIdIn(categoryIds.toMutableList())
        if (existingCount != categoryIds.size.toLong()) throw TrendCategoryNotFoundException()
    }
}
