package net.thechance.identity.api.dto

import net.thechance.identity.entity.User
import java.time.LocalDateTime
import java.util.*

data class UserResponse(
    val id: UUID,
    val firstName: String,
    val lastName: String,
    val phoneNumber: String,
    val lastLoginAt: LocalDateTime,
    val lastVisitAt: LocalDateTime,
    val status: User.Status
)