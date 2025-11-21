package net.thechance.trends.utils.repository_behaviour_mock

import io.mockk.every
import net.thechance.trends.entity.Trend
import net.thechance.trends.repository.TrendsRepository
import java.util.UUID

object TrendRepositoryMockBehaviour {
    fun mockFindAllById(
        trendsRepository: TrendsRepository,
        trendsList: List<Trend>
    ) {
        every { trendsRepository.findAllById(any<List<UUID>>()) } answers {
            val ids = firstArg<List<UUID>>()
            trendsList.filter { it.id in ids }
        }
    }
}