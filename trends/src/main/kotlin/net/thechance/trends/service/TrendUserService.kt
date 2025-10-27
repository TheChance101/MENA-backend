package net.thechance.trends.service

import net.thechance.trends.api.dto.base.PatchMetadata
import net.thechance.trends.api.dto.category.toUserSelectedCategories
import net.thechance.trends.entity.TrendUser
import net.thechance.trends.exception.InvalidTrendInputException
import net.thechance.trends.exception.TrendCategoryNotFoundException
import net.thechance.trends.exception.TrendUserNotFoundException
import net.thechance.trends.models.UserSelectedCategories
import net.thechance.trends.repository.CategoryRepository
import net.thechance.trends.repository.TrendUserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*
import kotlin.jvm.optionals.getOrElse
import kotlin.jvm.optionals.getOrNull

@Service
@Transactional
class TrendUserService(
    private val trendUserRepository: TrendUserRepository,
    private val categoryRepository: CategoryRepository,
    private val categoryService: CategoryService,
) {
    fun saveCategoriesToUser(userId: UUID, categoryIds: List<UUID>) {
        validateCategoriesNotEmpty(categoryIds)
        validateCategoriesExist(categoryIds)

        val trendUser = getOrCreateUser(userId)
        val categoryProxies = categoryIds.map { categoryId ->
            categoryRepository.getReferenceById(categoryId)
        }.toMutableSet()

        val updatedUser = trendUser.copy(categories = categoryProxies)
        trendUserRepository.save(updatedUser)
    }

    fun updateUserCategories(
        userId: UUID,
        categoriesToAdd: List<UUID>,
        categoriesToRemove: List<UUID>
    ): PatchMetadata {
        val allCategoryIds = (categoriesToAdd + categoriesToRemove).distinct()

        validateCategoriesNotEmpty(allCategoryIds)
        validateCategoriesExist(allCategoryIds)

        val trendUser = getUserOrThrow(userId)
        val currentCategoryIds = trendUser.categories.mapTo(mutableSetOf()) { it.id }

        val actualRemoved = categoriesToRemove.filterTo(mutableSetOf()) { it in currentCategoryIds }
        val actualAdded = categoriesToAdd.filterTo(mutableSetOf()) { it !in currentCategoryIds }

        if (actualAdded.isEmpty() && actualRemoved.isEmpty()) {
            return PatchMetadata(addedCount = 0, removedCount = 0)
        }

        val updatedCategories = trendUser.categories
            .filterNot { it.id in actualRemoved }
            .plus(actualAdded.map { categoryRepository.getReferenceById(it) })
            .toMutableSet()

        trendUserRepository.save(trendUser.copy(categories = updatedCategories))

        return PatchMetadata(addedCount = actualAdded.size, removedCount = actualRemoved.size)
    }

    fun getUserSelectedCategories(userId: UUID): List<UserSelectedCategories> {
        val allCategories = categoryService.getAllCategories()
        val userCategories = trendUserRepository.findById(userId).getOrNull()?.categories.orEmpty()

        return allCategories.map { category ->
            category.toUserSelectedCategories(isSelected = category in userCategories)
        }
    }

    private fun getUserOrThrow(userId: UUID) =
        trendUserRepository.findById(userId).getOrNull() ?: throw TrendUserNotFoundException()

    private fun getOrCreateUser(userId: UUID) =
        trendUserRepository.findById(userId).getOrElse { TrendUser(userId = userId) }

    private fun validateCategoriesNotEmpty(categoryIds: List<UUID>) {
        if (categoryIds.isEmpty()) throw InvalidTrendInputException()
    }

    private fun validateCategoriesExist(categoryIds: List<UUID>) {
        val existingCount = categoryRepository.countByIdIn(categoryIds.toMutableList())
        if (existingCount != categoryIds.size.toLong()) throw TrendCategoryNotFoundException()
    }
}
