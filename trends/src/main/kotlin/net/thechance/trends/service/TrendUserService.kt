package net.thechance.trends.service

import net.thechance.trends.entity.Category
import net.thechance.trends.repository.TrendUserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
@Transactional
class TrendUserService(
    private val trendUserRepository: TrendUserRepository
) {
    fun saveCategoriesToUser(userId: UUID, categories: List<Category>) {
        val trendUser = trendUserRepository.findById(userId).orElseThrow{ NoSuchElementException("User with id $userId does not exist") }
        val existingCategoryIds = trendUser.categories.map { it.id }.toSet()
        val newCategories = categories.filterNot { it.id in existingCategoryIds }

        trendUser.categories.addAll(newCategories)
        trendUserRepository.save(trendUser)
    }
}