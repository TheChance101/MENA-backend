package net.thechance.identity.service.mapper

import net.thechance.identity.entity.PrivacyAndPolicy
import net.thechance.identity.service.model.PrivacyAndPolicySectionModel

fun PrivacyAndPolicy.toModel() = PrivacyAndPolicySectionModel(title = title, content = content)

fun List<PrivacyAndPolicy>.toModel() = map { it.toModel() }
