package net.thechance.events.identity

import net.thechance.events.MenaEvent
import net.thechance.events.identity.utils.Gender
import net.thechance.events.identity.utils.Status
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

data class UserUpdatedEvent(
    val id: UUID,
    val phoneNumber: String,
    val password: String,
    val firstName: String,
    val lastName: String,
    val username: String,
    val imageUrl: String?,
    val birthDate: LocalDate,
    val lastLoginAt: LocalDateTime,
    val lastVisitAt: LocalDateTime,
    val gender: Gender,
    val status: Status
) : MenaEvent
