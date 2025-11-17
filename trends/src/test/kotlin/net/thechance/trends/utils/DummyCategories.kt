package net.thechance.trends.utils

import net.thechance.trends.entity.Category
import java.util.UUID


object DummyCategories {
    val technology = Category(
        id = UUID.fromString("00000000-0000-0000-0000-000000000001"),
        name = "technology",
        emoji = "💻"
    )

    val sports = Category(
        id = UUID.fromString("00000000-0000-0000-0000-000000000002"),
        name = "sports",
        emoji = "⚽"
    )

    val nature = Category(
        id = UUID.fromString("00000000-0000-0000-0000-000000000003"),
        name = "nature",
        emoji = "🌿"
    )

    val productivity = Category(
        id = UUID.fromString("00000000-0000-0000-0000-000000000004"),
        name = "productivity",
        emoji = "📊"
    )

    val fashion = Category(
        id = UUID.fromString("00000000-0000-0000-0000-000000000005"),
        name = "fashion",
        emoji = "👗"
    )

    val science = Category(
        id = UUID.fromString("00000000-0000-0000-0000-000000000006"),
        name = "science",
        emoji = "🔬"
    )
}