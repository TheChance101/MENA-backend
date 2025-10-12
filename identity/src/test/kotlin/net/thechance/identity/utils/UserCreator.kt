package net.thechance.identity.utils

import net.thechance.identity.entity.User
import java.util.UUID

fun createUser(
    id: UUID = UUID.randomUUID(),
    phoneNumber: String = "+201122334455",
    password: String = "test12300",
    firstName: String = "Thoraya",
    lastName: String = "Hamdy",
    username: String = "ss",
    imageUrl: String = ""
): User {
    return User(
        id = id,
        phoneNumber = phoneNumber,
        password = password,
        firstName = firstName,
        lastName = lastName,
        username = username,
        imageUrl = imageUrl
    )
}