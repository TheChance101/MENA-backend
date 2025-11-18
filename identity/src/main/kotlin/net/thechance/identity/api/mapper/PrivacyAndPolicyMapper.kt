package net.thechance.identity.api.mapper

import net.thechance.identity.api.dto.settings.PrivacyAndPolicyResponse
import net.thechance.identity.api.dto.settings.PrivacyAndPolicySection
import net.thechance.identity.service.model.PrivacyAndPolicyModel
import net.thechance.identity.service.model.PrivacyAndPolicySectionModel

fun PrivacyAndPolicyModel.toResponse() = PrivacyAndPolicyResponse(updatedAt = updatedAt, sections = sections.toResponse())

fun List<PrivacyAndPolicySectionModel>.toResponse() = map { PrivacyAndPolicySection(title = it.title, content = it.content) }
