package net.thechance.trends.service

import com.google.common.truth.Truth.assertThat
import io.mockk.mockk
import net.thechance.trends.api.dto.analytics.SubmitWatchTimeRequest
import net.thechance.trends.api.dto.analytics.WatchTimeDto
import net.thechance.trends.entity.Trend
import net.thechance.trends.entity.UserCategories
import net.thechance.trends.exception.TrendUserUnauthorizedException
import net.thechance.trends.repository.CategoryRepository
import net.thechance.trends.repository.TrendsRepository
import net.thechance.trends.repository.UserCategoryRepository
import net.thechance.trends.utils.DummyCategories
import net.thechance.trends.utils.DummyTrendUsers
import net.thechance.trends.utils.DummyTrends
import net.thechance.trends.utils.DummyUserCategories
import net.thechance.trends.utils.repository_behaviour_mock.TrendRepositoryMockBehaviour
import net.thechance.trends.utils.repository_behaviour_mock.UserCategoryRepositoryMockBehaviour
import org.junit.Assert.assertThrows
import org.junit.Before
import java.time.LocalDateTime
import java.util.*
import kotlin.test.Test

class TrendUserServiceTest {
    private val trendsRepository: TrendsRepository = mockk(relaxed = true)
    private val userCategoryRepository: UserCategoryRepository = mockk(relaxed = true)
    private val categoryService: CategoryService = mockk(relaxed = true)
    private val categoryRepository: CategoryRepository = mockk(relaxed = true)


    private val trendUserService: TrendUserService = TrendUserService(
        categoryRepository = categoryRepository,
        categoryService = categoryService,
        userCategoryRepository = userCategoryRepository,
        trendsRepository = trendsRepository
    )

    private val inMemoryUserCategories = mutableListOf<UserCategories>()
    private val inMemoryTrends = mutableListOf<Trend>()

    @Before
    fun setup() {
        inMemoryTrends.clear()
        inMemoryTrends.addAll(
            listOf(
                DummyTrends.trend1,
                DummyTrends.trend2,
                DummyTrends.trend3,
                DummyTrends.trend4
            )
        )

        inMemoryUserCategories.clear()

        TrendRepositoryMockBehaviour.mockFindAllById(trendsRepository, inMemoryTrends)
        UserCategoryRepositoryMockBehaviour.mockFindUserCategoriesByUserIdAndCategoryIdIn(userCategoryRepository, inMemoryUserCategories)
        UserCategoryRepositoryMockBehaviour.mockSaveAll(userCategoryRepository, inMemoryUserCategories)
    }

    @Test
    fun `when new user watches trend for first time should calculate initial affinity`() {
        val userId = DummyTrendUsers.user4.userId
        val request = SubmitWatchTimeRequest(
            userId = userId,
            watchTimes = listOf(
                WatchTimeDto(
                    trendId = DummyTrends.trend1.id,
                    percentWatched = 0.95
                )
            )
        )

        trendUserService.updateUserAffinities(userId, request)

        val savedCategory = inMemoryUserCategories.find {
            it.userId == userId && it.categoryId == DummyCategories.technology.id
        }

        assertThat(savedCategory).isNotNull()
        assertThat(savedCategory!!.affinity).isEqualTo(5)
        assertThat(savedCategory.isSelected).isFalse()
    }

    @Test
    fun `when new user watches multiple trends in same category should accumulate affinity`() {
        val userId = DummyTrendUsers.user4.userId
        val request = SubmitWatchTimeRequest(
            userId = userId,
            watchTimes = listOf(
                WatchTimeDto(trendId = DummyTrends.trend1.id, percentWatched = 0.95), // tech: +5
                WatchTimeDto(trendId = DummyTrends.trend1.id, percentWatched = 0.75), // tech: +4
                WatchTimeDto(trendId = DummyTrends.trend1.id, percentWatched = 0.55)  // tech: +3
            )
        )

        trendUserService.updateUserAffinities(userId, request)

        val savedCategory = inMemoryUserCategories.find {
            it.userId == userId && it.categoryId == DummyCategories.technology.id
        }

        assertThat(savedCategory!!.affinity).isEqualTo(12) // 5 + 4 + 3
        assertThat(savedCategory.isSelected).isFalse()
    }

    @Test
    fun `when new user accumulates high affinity should cap at 100`() {
        val userId = DummyTrendUsers.user4.userId
        val watchTimes = (1..25).map {
            WatchTimeDto(trendId = DummyTrends.trend1.id, percentWatched = 0.95)
        }
        val request = SubmitWatchTimeRequest(userId = userId, watchTimes = watchTimes)

        trendUserService.updateUserAffinities(userId, request)

        val savedCategory = inMemoryUserCategories.find {
            it.userId == userId && it.categoryId == DummyCategories.technology.id
        }

        assertThat(savedCategory!!.affinity).isEqualTo(100) // Capped at 100, not 125
    }

    @Test
    fun `when existing user watches trend on same day should not apply decay`() {
        val userId = DummyTrendUsers.user1.userId
        inMemoryUserCategories.add(DummyUserCategories.user1TechnologySelected)

        val request = SubmitWatchTimeRequest(
            userId = userId,
            watchTimes = listOf(
                WatchTimeDto(trendId = DummyTrends.trend1.id, percentWatched = 0.95)
            )
        )

        trendUserService.updateUserAffinities(userId, request)

        val updatedCategory = inMemoryUserCategories.find {
            it.userId == userId && it.categoryId == DummyCategories.technology.id
        }

        assertThat(updatedCategory!!.affinity).isEqualTo(35) // 30 + 5, no decay
    }

    @Test
    fun `when existing user watches trend after 1 day should apply single day decay`() {
        val userId = DummyTrendUsers.user1.userId
        val oldCategory = UserCategories(
            userId = userId,
            categoryId = DummyCategories.technology.id,
            isSelected = true,
            affinity = 50,
            lastUpdated = LocalDateTime.now().minusDays(1)
        )
        inMemoryUserCategories.add(oldCategory)

        val request = SubmitWatchTimeRequest(
            userId = userId,
            watchTimes = listOf(
                WatchTimeDto(trendId = DummyTrends.trend1.id, percentWatched = 0.95)
            )
        )

        trendUserService.updateUserAffinities(userId, request)

        val updatedCategory = inMemoryUserCategories.find {
            it.userId == userId && it.categoryId == DummyCategories.technology.id
        }

        // 50 * 0.99 = 49.5 -> 49, then + 5 = 54
        assertThat(updatedCategory!!.affinity).isEqualTo(54)
    }

    @Test
    fun `when existing user watches trend after 30 days should apply 30 day decay`() {
        val userId = DummyTrendUsers.user1.userId
        val oldCategory = UserCategories(
            userId = userId,
            categoryId = DummyCategories.technology.id,
            isSelected = true,
            affinity = 50,
            lastUpdated = LocalDateTime.now().minusDays(30)
        )
        inMemoryUserCategories.add(oldCategory)

        val request = SubmitWatchTimeRequest(
            userId = userId,
            watchTimes = listOf(
                WatchTimeDto(trendId = DummyTrends.trend1.id, percentWatched = 0.95)
            )
        )

        trendUserService.updateUserAffinities(userId, request)

        val updatedCategory = inMemoryUserCategories.find {
            it.userId == userId && it.categoryId == DummyCategories.technology.id
        }

        // 50 * (0.99^30) ≈ 50 * 0.7397 ≈ 36.98 -> 36, then + 5 = 41
        assertThat(updatedCategory!!.affinity).isIn(41..42)
    }

    @Test
    fun `when existing user watches trend after 100 days should apply heavy decay`() {
        val userId = DummyTrendUsers.user2.userId
        inMemoryUserCategories.add(
            DummyUserCategories.user2TechnologyDecayed.copy(
                lastUpdated = LocalDateTime.now().minusDays(100)
            )
        )

        val request = SubmitWatchTimeRequest(
            userId = userId,
            watchTimes = listOf(
                WatchTimeDto(trendId = DummyTrends.trend1.id, percentWatched = 0.95)
            )
        )

        trendUserService.updateUserAffinities(userId, request)

        val updatedCategory = inMemoryUserCategories.find {
            it.userId == userId && it.categoryId == DummyCategories.technology.id
        }

        // 100 * (0.99^100) ≈ 100 * 0.366 ≈ 36, then + 5 = 41
        assertThat(updatedCategory!!.affinity).isIn(40..42)
    }

    @Test
    fun `when existing user exceeds 100 affinity after decay should cap at 100`() {
        val userId = DummyTrendUsers.user2.userId
        val oldCategory = UserCategories(
            userId = userId,
            categoryId = DummyCategories.technology.id,
            isSelected = true,
            affinity = 98,
            lastUpdated = LocalDateTime.now().minusDays(1)
        )
        inMemoryUserCategories.add(oldCategory)

        val request = SubmitWatchTimeRequest(
            userId = userId,
            watchTimes = listOf(
                WatchTimeDto(trendId = DummyTrends.trend1.id, percentWatched = 0.95)
            )
        )

        trendUserService.updateUserAffinities(userId, request)

        val updatedCategory = inMemoryUserCategories.find {
            it.userId == userId && it.categoryId == DummyCategories.technology.id
        }

        assertThat(updatedCategory!!.affinity).isEqualTo(100) // Capped at 100
    }

    @Test
    fun `when user watches trend with multiple categories should update independent affinities`() {
        val userId = DummyTrendUsers.user1.userId
        inMemoryUserCategories.add(
            UserCategories(
                userId = userId,
                categoryId = DummyCategories.technology.id,
                isSelected = true,
                affinity = 30,
                lastUpdated = LocalDateTime.now()
            )
        )
        inMemoryUserCategories.add(
            UserCategories(
                userId = userId,
                categoryId = DummyCategories.sports.id,
                isSelected = true,
                affinity = 70,
                lastUpdated = LocalDateTime.now()
            )
        )

        val request = SubmitWatchTimeRequest(
            userId = userId,
            watchTimes = listOf(
                WatchTimeDto(trendId = DummyTrends.trend3.id, percentWatched = 0.95) // tech, sports, nature
            )
        )

        trendUserService.updateUserAffinities(userId, request)

        val techCategory = inMemoryUserCategories.find {
            it.userId == userId && it.categoryId == DummyCategories.technology.id
        }
        val sportsCategory = inMemoryUserCategories.find {
            it.userId == userId && it.categoryId == DummyCategories.sports.id
        }
        val natureCategory = inMemoryUserCategories.find {
            it.userId == userId && it.categoryId == DummyCategories.nature.id
        }

        assertThat(techCategory!!.affinity).isEqualTo(35)
        assertThat(sportsCategory!!.affinity).isEqualTo(75)
        assertThat(natureCategory!!.affinity).isEqualTo(5) // New category
        assertThat(natureCategory.isSelected).isFalse()
    }

    @Test
    fun `when user watches trend with categories having different decay rates should apply decay independently`() {
        val userId = DummyTrendUsers.user1.userId
        inMemoryUserCategories.add(
            UserCategories(
                userId = userId,
                categoryId = DummyCategories.technology.id,
                isSelected = true,
                affinity = 50,
                lastUpdated = LocalDateTime.now()
            )
        )
        inMemoryUserCategories.add(
            UserCategories(
                userId = userId,
                categoryId = DummyCategories.sports.id,
                isSelected = true,
                affinity = 50,
                lastUpdated = LocalDateTime.now().minusDays(10)
            )
        )

        val request = SubmitWatchTimeRequest(
            userId = userId,
            watchTimes = listOf(
                WatchTimeDto(trendId = DummyTrends.trend3.id, percentWatched = 0.95)
            )
        )

        trendUserService.updateUserAffinities(userId, request)

        val techCategory = inMemoryUserCategories.find {
            it.userId == userId && it.categoryId == DummyCategories.technology.id
        }
        val sportsCategory = inMemoryUserCategories.find {
            it.userId == userId && it.categoryId == DummyCategories.sports.id
        }

        assertThat(techCategory!!.affinity).isEqualTo(55) // No decay
        assertThat(sportsCategory!!.affinity).isIn(49..51) // With 10-day decay
    }

    @Test
    fun `when user watches non-existent trend should skip it`() {
        val userId = DummyTrendUsers.user1.userId
        val nonExistentTrendId = UUID.randomUUID()

        val request = SubmitWatchTimeRequest(
            userId = userId,
            watchTimes = listOf(
                WatchTimeDto(trendId = nonExistentTrendId, percentWatched = 0.95)
            )
        )

        trendUserService.updateUserAffinities(userId, request)

        assertThat(inMemoryUserCategories.none { it.userId == userId }).isTrue()
    }

    @Test
    fun `when user watches trends with mixed engagement levels should calculate correct affinity scores`() {
        val userId = DummyTrendUsers.user4.userId
        val request = SubmitWatchTimeRequest(
            userId = userId,
            watchTimes = listOf(
                WatchTimeDto(trendId = DummyTrends.trend1.id, percentWatched = 0.95), // +5
                WatchTimeDto(trendId = DummyTrends.trend1.id, percentWatched = 0.75), // +4
                WatchTimeDto(trendId = DummyTrends.trend1.id, percentWatched = 0.55), // +3
                WatchTimeDto(trendId = DummyTrends.trend1.id, percentWatched = 0.35), // +2
                WatchTimeDto(trendId = DummyTrends.trend1.id, percentWatched = 0.18)  // +1
            )
        )

        trendUserService.updateUserAffinities(userId, request)

        val savedCategory = inMemoryUserCategories.find {
            it.userId == userId && it.categoryId == DummyCategories.technology.id
        }

        assertThat(savedCategory!!.affinity).isEqualTo(15) // 5+4+3+2+1
    }

    @Test
    fun `when user watches trend at percent below boundary should calculate engagement correctly`() {
        val userId = DummyTrendUsers.user4.userId

        // Test 0.899 -> score 4
        val request = SubmitWatchTimeRequest(
            userId = userId,
            watchTimes = listOf(WatchTimeDto(trendId = DummyTrends.trend1.id, percentWatched = 0.899))
        )
        trendUserService.updateUserAffinities(userId, request)
        assertThat(inMemoryUserCategories.first().affinity).isEqualTo(4)
    }

    @Test
    fun `when user watches trend at percent exact as boundary should calculate engagement correctly`() {
        val userId = DummyTrendUsers.user4.userId

        // Test 0.900 -> score 5
        val request = SubmitWatchTimeRequest(
            userId = userId,
            watchTimes = listOf(WatchTimeDto(trendId = DummyTrends.trend1.id, percentWatched = 0.900))
        )
        trendUserService.updateUserAffinities(userId, request)
        assertThat(inMemoryUserCategories.first().affinity).isEqualTo(5)
    }

    @Test
    fun `when unauthorized user attempts to update affinities should throw exception`() {
        val authenticatedUserId = DummyTrendUsers.user1.userId
        val requestUserId = DummyTrendUsers.user2.userId

        val request = SubmitWatchTimeRequest(
            userId = requestUserId,
            watchTimes = listOf(
                WatchTimeDto(trendId = DummyTrends.trend1.id, percentWatched = 0.95)
            )
        )

        assertThrows(TrendUserUnauthorizedException::class.java) {
            trendUserService.updateUserAffinities(authenticatedUserId, request)
        }

        assertThat(inMemoryUserCategories).isEmpty()
    }

    @Test
    fun `when user watches trend with mixed category states should update all categories correctly`() {
        val userId = DummyTrendUsers.user1.userId

        // Category 1 (technology): selected, affinity = 40
        inMemoryUserCategories.add(
            UserCategories(
                userId = userId,
                categoryId = DummyCategories.technology.id,
                isSelected = true,
                affinity = 40,
                lastUpdated = LocalDateTime.now()
            )
        )

        // Category 2 (sports): NOT selected, affinity = 20
        inMemoryUserCategories.add(
            UserCategories(
                userId = userId,
                categoryId = DummyCategories.sports.id,
                isSelected = false,
                affinity = 20,
                lastUpdated = LocalDateTime.now()
            )
        )

        // Category 3 (nature): doesn't exist yet

        val request = SubmitWatchTimeRequest(
            userId = userId,
            watchTimes = listOf(
                WatchTimeDto(trendId = DummyTrends.trend3.id, percentWatched = 0.95) // tech, sports, nature
            )
        )

        trendUserService.updateUserAffinities(userId, request)

        val techCategory = inMemoryUserCategories.find {
            it.userId == userId && it.categoryId == DummyCategories.technology.id
        }
        val sportsCategory = inMemoryUserCategories.find {
            it.userId == userId && it.categoryId == DummyCategories.sports.id
        }
        val natureCategory = inMemoryUserCategories.find {
            it.userId == userId && it.categoryId == DummyCategories.nature.id
        }

        // Category 1: was selected, affinity updated
        assertThat(techCategory).isNotNull()
        assertThat(techCategory!!.affinity).isEqualTo(45) // 40 + 5
        assertThat(techCategory.isSelected).isTrue() // remains selected

        // Category 2: was NOT selected, but affinity still updated
        assertThat(sportsCategory).isNotNull()
        assertThat(sportsCategory!!.affinity).isEqualTo(25) // 20 + 5
        assertThat(sportsCategory.isSelected).isFalse() // remains not selected

        // Category 3: new category, affinity created, isSelected = false
        assertThat(natureCategory).isNotNull()
        assertThat(natureCategory!!.affinity).isEqualTo(5) // new with engagement score 5
        assertThat(natureCategory.isSelected).isFalse() // new categories default to false
    }
}

