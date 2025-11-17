package net.thechance.trends.utils.repository_behaviour_mock

import io.mockk.every
import net.thechance.trends.entity.UserCategories
import net.thechance.trends.repository.UserCategoryRepository
import java.util.UUID

object UserCategoryRepositoryMockBehaviour {

    fun mockFindUserCategoriesByUserIdAndCategoryIdIn(
        userCategoryRepository: UserCategoryRepository,
        userCategoryList: MutableList<UserCategories>
    ) {
        every { userCategoryRepository.findAllByUserIdAndCategoryIdIn(any(), any()) } answers {
            val userId = firstArg<UUID>()
            val categoryIds = secondArg<Set<UUID>>()

            userCategoryList.filter {
                it.userId == userId && it.categoryId in categoryIds
            }.toMutableList()
        }
    }

    fun mockSaveAll(
        userCategoryRepository: UserCategoryRepository,
        userCategoryList: MutableList<UserCategories>
    ) {
        every { userCategoryRepository.saveAll(any<List<UserCategories>>()) } answers {
            val toSave = firstArg<List<UserCategories>>()

            toSave.forEach { newCategory ->
                val existingIndex = userCategoryList.indexOfFirst {
                    it.userId == newCategory.userId &&
                            it.categoryId == newCategory.categoryId
                }

                if (existingIndex != -1)
                    userCategoryList[existingIndex] = newCategory
                else
                    userCategoryList.add(newCategory)
            }

            toSave
        }
    }
}