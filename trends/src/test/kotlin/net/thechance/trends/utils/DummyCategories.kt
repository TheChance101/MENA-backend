package net.thechance.trends.utils

import net.thechance.trends.entity.Category
import java.util.UUID


object DummyCategories {
    val technology = Category(
        id = UUID.randomUUID(),
        name = "technology",
        emoji = "💻"
    )

    val sports = Category(
        id = UUID.randomUUID(),
        name = "sports",
        emoji = "⚽"
    )

    val nature = Category(
        id = UUID.randomUUID(),
        name = "nature",
        emoji = "🌿"
    )

    val productivity = Category(
        id = UUID.randomUUID(),
        name = "productivity",
        emoji = "📊"
    )

    val fashion = Category(
        id = UUID.randomUUID(),
        name = "fashion",
        emoji = "👗"
    )

    val science = Category(
        id = UUID.randomUUID(),
        name = "science",
        emoji = "🔬"
    )
}