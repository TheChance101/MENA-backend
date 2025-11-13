package net.thechance.identity.api.dto.settings

import java.time.Instant

class PrivacyAndPolicyResponse(
    val updatedAt: Instant?,
    val sections: List<PrivacyAndPolicySection>
)

class PrivacyAndPolicySection(
    val title: String,
    val content: String
)
