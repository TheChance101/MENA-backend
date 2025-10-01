package net.thechance.trends.service

import net.thechance.trends.exception.InvalidTrendInputException
import net.thechance.trends.exception.TrendCategoryNotFoundException
import net.thechance.trends.entity.TrendUser
import net.thechance.trends.repository.CategoryRepository
import net.thechance.trends.repository.TrendUserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID
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

    fun getDoesUserHaveCategories(userId: UUID): Boolean {
        return trendUserRepository.findById(userId).getOrNull()?.categories?.isNotEmpty() ?: false
    }
}