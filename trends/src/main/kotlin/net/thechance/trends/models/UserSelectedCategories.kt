package net.thechance.trends.models

import java.util.UUID

data class UserSelectedCategories(
    val id: UUID,
    val name: String,
    val emoji: String,
    val isSelected: Boolean
)