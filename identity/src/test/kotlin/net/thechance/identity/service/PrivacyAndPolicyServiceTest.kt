package net.thechance.identity.service

import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import net.thechance.identity.repository.PrivacyAndPolicyRepository
import net.thechance.identity.service.model.PrivacyAndPolicyModel
import net.thechance.identity.utils.createPrivacyAndPolicy
import net.thechance.identity.utils.createPrivacyAndPolicySectionModel
import org.junit.Test
import java.time.Instant

class PrivacyAndPolicyServiceTest {
    private val privacyAndPolicyRepository: PrivacyAndPolicyRepository = mockk(relaxed = true)
    private val privacyAndPolicyService = PrivacyAndPolicyService(privacyAndPolicyRepository = privacyAndPolicyRepository)

    @Test
    fun `getPrivacyAndPolicy() should return empty list and null instant when no records in DB matches the language`() {
        every { privacyAndPolicyRepository.getAllByLanguageOrderByShowOrder(any()) } returns emptyList()

        val result = privacyAndPolicyService.getPrivacyAndPolicy(LANGUAGE)

        assertThat(result).isEqualTo(emptyPrivacyAndPolicyModel)
    }

    @Test
    fun `getPrivacyAndPolicy() should return data when records in DB matches the language`() {
        every { privacyAndPolicyRepository.getAllByLanguageOrderByShowOrder(any()) } returns sections

        val result = privacyAndPolicyService.getPrivacyAndPolicy(LANGUAGE).sections

        assertThat(result).isEqualTo(sectionsModel)
    }

    @Test
    fun `getPrivacyAndPolicy() should return last update date when records in DB matches the language`() {
        every { privacyAndPolicyRepository.getAllByLanguageOrderByShowOrder(any()) } returns sections

        val result = privacyAndPolicyService.getPrivacyAndPolicy(LANGUAGE).updatedAt?.epochSecond

        assertThat(result).isEqualTo(updatedAt.epochSecond)
    }

    companion object {
        private const val LANGUAGE = "en"
        private const val DAY_IN_SECONDS = 60 * 60 * 24L
        private val updatedAt = Instant.now().plusSeconds(DAY_IN_SECONDS)

        private val sections = listOf(
            createPrivacyAndPolicy(title = "Title 1", content = "Content 1"),
            createPrivacyAndPolicy(title = "Title 2", content = "Content 2", updatedAt = updatedAt, createdAt = updatedAt),
            createPrivacyAndPolicy(title = "Title 3", content = "Content 3"),
        )

        private val sectionsModel = listOf(
            createPrivacyAndPolicySectionModel(title = "Title 1", content = "Content 1"),
            createPrivacyAndPolicySectionModel(title = "Title 2", content = "Content 2"),
            createPrivacyAndPolicySectionModel(title = "Title 3", content = "Content 3"),
        )

        private val emptyPrivacyAndPolicyModel = PrivacyAndPolicyModel(
            updatedAt = null,
            sections = emptyList()
        )
    }
}