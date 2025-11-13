package net.thechance.identity.service

import net.thechance.identity.entity.PrivacyAndPolicy
import net.thechance.identity.repository.PrivacyAndPolicyRepository
import net.thechance.identity.service.mapper.toModel
import net.thechance.identity.service.model.PrivacyAndPolicyModel
import org.springframework.stereotype.Service

@Service
class PrivacyAndPolicyService(
    private val privacyAndPolicyRepository: PrivacyAndPolicyRepository
) {
    fun getPrivacyAndPolicy(language: String): PrivacyAndPolicyModel {
        privacyAndPolicyRepository.getAllByLanguageOrderByShowOrder(language).let { sections ->
            sections.getLastUpdatedSection().also { lastUpdatedSection ->
                return PrivacyAndPolicyModel(
                    updatedAt = lastUpdatedSection?.updatedAt,
                    sections = sections.toModel(),
                )
            }
        }
    }

    private fun List<PrivacyAndPolicy>.getLastUpdatedSection() = maxByOrNull { it.updatedAt }
}
