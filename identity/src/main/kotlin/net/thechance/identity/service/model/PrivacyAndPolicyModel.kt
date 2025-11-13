package net.thechance.identity.service.model

import java.time.Instant

data class PrivacyAndPolicyModel(
    val updatedAt: Instant?,
    val sections: List<PrivacyAndPolicySectionModel>
)

data class PrivacyAndPolicySectionModel(
    val title: String,
    val content: String
)
