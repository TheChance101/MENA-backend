package net.thechance.events.identity

import net.thechance.events.MenaEvent
import net.thechance.events.identity.utils.Gender
import net.thechance.events.identity.utils.Status
import java.time.LocalDate

data class UserCreatedEvent(
    val phoneNumber: String,
    val password: String,
    val firstName: String,
    val lastName: String,
    val username: String,
    val imageUrl: String?,
    val birthDate: LocalDate,
    val gender: Gender,
    val status: Status
) : MenaEvent