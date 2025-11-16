package net.thechance.trends.service

import net.thechance.trends.api.dto.analytics.SubmitWatchTimeRequest
import net.thechance.trends.api.dto.base.PatchMetadata
import net.thechance.trends.api.dto.category.toUserSelectedCategories
import net.thechance.trends.entity.UserCategories
import net.thechance.trends.exception.InvalidTrendInputException
import net.thechance.trends.exception.TrendCategoryNotFoundException
import net.thechance.trends.models.UserSelectedCategories
import net.thechance.trends.repository.CategoryRepository
import net.thechance.trends.repository.TrendsRepository
import net.thechance.trends.repository.UserCategoryRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.util.*
import kotlin.math.pow

@Service
@Transactional
class TrendUserService(
    private val categoryRepository: CategoryRepository,
    private val categoryService: CategoryService,
    private val userCategoryRepository: UserCategoryRepository,
    private val trendsRepository: TrendsRepository
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

    fun updateUserAffinities(userId: UUID, watchTimeRequest: SubmitWatchTimeRequest) {
        val trendIds = watchTimeRequest.watchTimes.map { it.trendId }
        val trends = trendsRepository.findAllById(trendIds)
        val trendsMap = trends.associateBy { it.id }

        val categoryIds = trends.flatMap { it.categories.map { category -> category.id } }.toMutableSet()

        val existingUserCategories = userCategoryRepository.findAllByUserIdAndCategoryIdIn(userId, categoryIds)
        val userCategoryMap = existingUserCategories.associateBy { it.categoryId }.toMutableMap()

        val categoryEngagementScores = mutableMapOf<UUID, MutableList<Int>>()

        watchTimeRequest.watchTimes.forEach { watchTime ->
            val trend = trendsMap[watchTime.trendId] ?: return@forEach
            val engagementScore = calculateEngagementScore(watchTime.percentWatched)

            trend.categories.forEach { category ->
                categoryEngagementScores.getOrPut(category.id) { mutableListOf() }.add(engagementScore)
            }
        }

        val categoriesToSave = mutableListOf<UserCategories>()
        val now = LocalDateTime.now()

        categoryEngagementScores.forEach { (categoryId, scores) ->
            val totalEngagementScore = scores.sum()
            val existingCategory = userCategoryMap[categoryId]

            val newAffinity = if (existingCategory != null) {
                calculateNewAffinityWithDecay(existingCategory.lastUpdated,existingCategory.affinity, totalEngagementScore, now)
            } else {
                totalEngagementScore.coerceIn(0, 100)
            }

            val updatedCategory = existingCategory?.copy(
                affinity = newAffinity,
                lastUpdated = now
            ) ?: UserCategories(
                userId = userId,
                categoryId = categoryId,
                isSelected = false,
                affinity = newAffinity,
                lastUpdated = now
            )

            categoriesToSave.add(updatedCategory)
        }

        userCategoryRepository.saveAll(categoriesToSave)
    }

    private fun calculateEngagementScore(percentWatched: Double): Int {
        return when {
            percentWatched >= 0.9 -> 5
            percentWatched >= 0.7 -> 4
            percentWatched >= 0.5 -> 3
            percentWatched >= 0.3 -> 2
            percentWatched >= 0.15 -> 1
            else -> 0
        }
    }

    private fun calculateNewAffinityWithDecay(
        lastUpdated: LocalDateTime,
        currentAffinity: Int,
        engagementScore: Int,
        now: LocalDateTime
    ): Int {
        val daysSinceLastUpdate = ChronoUnit.DAYS.between(lastUpdated, now)

        val decayRate = 0.01
        val timeDecayFactor = (1 - decayRate).pow(daysSinceLastUpdate.toDouble())

        val decayedAffinity = (currentAffinity * timeDecayFactor).toInt()

        val newAffinity = (decayedAffinity + engagementScore).coerceIn(0, 100)

        return newAffinity
    }

    private fun validateCategoriesNotEmpty(categoryIds: List<UUID>) {
        if (categoryIds.isEmpty()) throw InvalidTrendInputException()
    }

    private fun validateCategoriesExist(categoryIds: List<UUID>) {
        val existingCount = categoryRepository.countByIdIn(categoryIds.toMutableList())
        if (existingCount != categoryIds.size.toLong()) throw TrendCategoryNotFoundException()
    }
}
