package net.thechance.identity.service.model

import java.util.UUID

data class UserServiceModel(
    val id: UUID,
    val phoneNumber: String,
    val username: String,
    val firstName: String,
    val lastName: String,
    val imageUrl: String?,
    val gender: Int,
    val birthDate: String
)