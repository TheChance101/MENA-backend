package net.thechance.trends.service

import net.thechance.trends.api.dto.PatchMetadata
import net.thechance.trends.entity.Category
import net.thechance.trends.entity.TrendUser
import net.thechance.trends.exception.InvalidTrendInputException
import net.thechance.trends.exception.TrendCategoryNotFoundException
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
    private val categoryRepository: CategoryRepository
) {
    fun saveCategoriesToUser(userId: UUID, categoryIds: List<UUID>) {

        if (categoryIds.isEmpty()) {
            throw InvalidTrendInputException()
        }

        val existingCategoryCount = categoryRepository.countByIdIn(categoryIds.toMutableList())
        if (existingCategoryCount != categoryIds.size.toLong()) {
            throw TrendCategoryNotFoundException()
        }

        val trendUser = trendUserRepository.findById(userId).getOrElse {
            TrendUser(userId = userId)
        }

        val categoryProxies = categoryIds.map { categoryId ->
            categoryRepository.getReferenceById(categoryId)
        }.toMutableSet()

        val updatedUser = trendUser.copy(categories = categoryProxies)
        trendUserRepository.save(updatedUser)
    }

    fun patchUserCategories(
        userId: UUID,
        categoriesToAdd: List<UUID>,
        categoriesToRemove: List<UUID>
    ): PatchMetadata {
        val allCategoryIds = (categoriesToAdd + categoriesToRemove).distinct()

        if (allCategoryIds.isNotEmpty()) {
            val existingCategoryCount = categoryRepository.countByIdIn(allCategoryIds.toMutableList())
            if (existingCategoryCount != allCategoryIds.size.toLong()) {
                throw TrendCategoryNotFoundException()
            }
        }

        val trendUser = trendUserRepository.findById(userId).getOrElse { TrendUser(userId = userId) }

        val currentCategories = trendUser.categories.toMutableSet()
        val currentCategoryIds = currentCategories.map { it.id }.toSet()

        var addedCount = 0
        var removedCount = 0

        categoriesToRemove.forEach { categoryId ->
            currentCategories.removeIf { it.id == categoryId }.let { removed ->
                if (removed) removedCount++
            }
        }

        categoriesToAdd.forEach { categoryId ->
            if (categoryId !in currentCategoryIds) {
                currentCategories.add(categoryRepository.getReferenceById(categoryId))
                addedCount++
            }
        }

        val updatedUser = trendUser.copy(categories = currentCategories)
        trendUserRepository.save(updatedUser)

        return PatchMetadata(addedCount = addedCount, removedCount = removedCount)
    }

    fun getUserSelectedCategories(userId: UUID): Set<Category> {
        return trendUserRepository.findById(userId).getOrNull()?.categories.orEmpty()
    }

    fun getDoesUserHaveCategories(userId: UUID): Boolean {
        return trendUserRepository.findById(userId).getOrNull()?.categories?.isNotEmpty() ?: false
    }
}