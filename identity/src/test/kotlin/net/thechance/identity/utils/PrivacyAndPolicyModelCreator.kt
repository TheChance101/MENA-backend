package net.thechance.identity.utils

import net.thechance.identity.service.model.PrivacyAndPolicySectionModel

fun createPrivacyAndPolicySectionModel(
    title: String = "Title",
    content: String = "Content"
): PrivacyAndPolicySectionModel {
    return PrivacyAndPolicySectionModel(
        title = title,
        content = content,
    )
}
