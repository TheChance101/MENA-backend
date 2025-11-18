package net.thechance.identity.service.model

import java.util.UUID

data class RegisterUserModel(
    val phoneNumber: String,
    val username: String,
    val firstName: String,
    val lastName: String,
    val birthDate: String,
    val gender: Int,
    val password: String,
    val sessionId: UUID
)
