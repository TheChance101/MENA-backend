package net.thechance.trends.utils

import net.thechance.trends.entity.TrendUser
import net.thechance.trends.entity.UserCategories
import java.time.LocalDateTime
import java.util.UUID

object DummyTrendUsers {

    // Has selected categories and has not uploaded any trends
    val user1 = TrendUser(
        phoneNumber = "+201293393331",
        firstName = "test",
        lastName = "",
        username = "",
        imageUrl = null,
        userId = UUID.randomUUID(),
        status = TrendUser.Status.ACTIVE
    )

    // Has selected categories and has uploaded trends
    val user2 = TrendUser(
        phoneNumber = "+201293393332",
        firstName = "",
        lastName = "",
        username = "",
        imageUrl = null,
        userId = UUID.randomUUID(),
        status = TrendUser.Status.ACTIVE
    )

    // Has no selected categories and has uploaded trends
    val user3 = TrendUser(
        phoneNumber = "+201293393333",
        firstName = "",
        lastName = "",
        username = "",
        imageUrl = null,
        userId = UUID.randomUUID(),
        status = TrendUser.Status.ACTIVE
    )
    
    // Has no selected categories and has not uploaded any trends
    val user4 = TrendUser(
        phoneNumber = "+201293393334",
        firstName = "",
        lastName = "",
        username = "",
        imageUrl = null,
        userId = UUID.randomUUID(),
        status = TrendUser.Status.ACTIVE
    )
}

object DummyUserCategories {
    val user1TechnologySelected = UserCategories(
        userId = DummyTrendUsers.user1.userId,
        categoryId = DummyCategories.technology.id,
        isSelected = true,
        affinity = 30,
        lastUpdated = LocalDateTime.now()
    )

    val user1SportsNotSelected = UserCategories(
        userId = DummyTrendUsers.user1.userId,
        categoryId = DummyCategories.sports.id,
        isSelected = false,
        affinity = 20,
        lastUpdated = LocalDateTime.now().minusDays(10)
    )

    val user2ProductivitySelected = UserCategories(
        userId = DummyTrendUsers.user2.userId,
        categoryId = DummyCategories.productivity.id,
        isSelected = true,
        affinity = 50,
        lastUpdated = LocalDateTime.now()
    )

    val user2TechnologyDecayed = UserCategories(
        userId = DummyTrendUsers.user2.userId,
        categoryId = DummyCategories.technology.id,
        isSelected = true,
        affinity = 100,
        lastUpdated = LocalDateTime.now().minusDays(30)
    )
}