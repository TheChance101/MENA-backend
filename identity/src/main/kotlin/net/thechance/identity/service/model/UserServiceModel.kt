package net.thechance.identity.service.model

import java.time.LocalDate
import java.util.UUID

data class UserServiceModel(
    val id: UUID,
    val username: String,
    val firstName: String,
    val lastName: String,
    val gender: Int,
    val birthDate: LocalDate,
    val imageUrl: String? = null,
    val phoneNumber: String = ""
)