package net.thechance.trends.repository

import net.thechance.trends.entity.UserCategories
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface UserCategoryRepository: JpaRepository<UserCategories, UUID>{
    fun findByUserIdAndCategoryId(userId: UUID, categoryId: UUID): UserCategories?
    fun findUserCategoriesByUserId(userId: UUID): MutableList<UserCategories>
}