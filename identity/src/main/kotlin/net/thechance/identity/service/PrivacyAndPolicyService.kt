package net.thechance.identity.service

import net.thechance.identity.service.model.PrivacyAndPolicyModel
import net.thechance.identity.service.model.PrivacyAndPolicySectionModel
import org.springframework.context.MessageSource
import org.springframework.stereotype.Service
import java.time.Instant
import java.util.Locale

@Service
class PrivacyAndPolicyService(private val messageSource: MessageSource) {
    fun getPrivacyAndPolicy(locale: Locale): PrivacyAndPolicyModel {
        val sections = getSections(locale)
        return PrivacyAndPolicyModel(updatedAt = updatedAt, sections = sections)
    }

    private fun getSections(locale: Locale): List<PrivacyAndPolicySectionModel> = (0..<MAX_SECTIONS).map { index ->
        val title = messageSource.getMessage("identity.privacy.policy.sections[$index].title", null, locale)
        val content = messageSource.getMessage("identity.privacy.policy.sections[$index].content", null, locale)
        PrivacyAndPolicySectionModel(title = title, content = content)
    }

    companion object {
        private val updatedAt = Instant.parse("2026-11-11T00:00:00Z")
        private const val MAX_SECTIONS = 3
    }
}
