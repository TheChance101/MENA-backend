package net.thechance.identity.utils

import net.thechance.identity.entity.PrivacyAndPolicy
import java.time.Instant
import java.util.UUID

fun createPrivacyAndPolicy(
    id: UUID = UUID.randomUUID(),
    title: String = "Title",
    content: String = "Content",
    language: String = "en",
    showOrder: UInt = 1U,
    createdAt: Instant = Instant.now(),
    updatedAt: Instant = Instant.now(),
): PrivacyAndPolicy {
    return PrivacyAndPolicy(
        id = id,
        title = title,
        content = content,
        language = language,
        showOrder = showOrder,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}
