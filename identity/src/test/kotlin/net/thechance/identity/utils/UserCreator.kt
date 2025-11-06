package net.thechance.identity.utils

import net.thechance.identity.entity.User
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

fun createUser(
    id: UUID = UUID.randomUUID(),
    phoneNumber: String = "+201122334455",
    password: String = "test12300",
    firstName: String = "Thoraya",
    lastName: String = "Hamdy",
    username: String = "ss",
    imageUrl: String = "",
    birthDate: LocalDate = LocalDate.now(),
    gender: Int = User.Gender.MALE.toInt()
): User {
    return User(
        id = id,
        phoneNumber = phoneNumber,
        password = password,
        firstName = firstName,
        lastName = lastName,
        username = username,
        imageUrl = imageUrl,
        birthDate = birthDate,
        gender = gender,
        lastVisitAt = LocalDateTime.now(),
        lastLoginAt = LocalDateTime.now(),
        status = User.Status.ACTIVE
    )
}