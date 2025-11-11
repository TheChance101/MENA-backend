package net.thechance.identity.service.mapper

import net.thechance.events.identity.UserCreatedEvent
import net.thechance.events.identity.UserStatusUpdatedEvent
import net.thechance.events.identity.UserUpdatedEvent
import net.thechance.events.identity.utils.Gender
import net.thechance.events.identity.utils.Status
import net.thechance.identity.entity.User

fun User.toUserCreatedEvent(): UserCreatedEvent {
    return UserCreatedEvent(
        id = id,
        phoneNumber = phoneNumber,
        password = password,
        firstName = firstName,
        lastName = lastName,
        username = username,
        imageUrl = imageUrl,
        birthDate = birthDate,
        lastLoginAt = lastLoginAt,
        lastVisitAt = lastVisitAt,
        gender = toUserEventGender(gender),
        status = status.toUserEventStatus()
    )
}

fun User.toUserUpdatedEvent(): UserUpdatedEvent {
    return UserUpdatedEvent(
        id = id,
        phoneNumber = phoneNumber,
        password = password,
        firstName = firstName,
        lastName = lastName,
        username = username,
        imageUrl = imageUrl,
        birthDate = birthDate,
        lastLoginAt = lastLoginAt,
        lastVisitAt = lastVisitAt,
        gender = toUserEventGender(gender),
        status = status.toUserEventStatus()
    )
}

fun User.Status.toEventStatus(): UserStatusUpdatedEvent.UserStatus {
    return when (this) {
        User.Status.ACTIVE -> UserStatusUpdatedEvent.UserStatus.ACTIVE
        User.Status.BLOCKED -> UserStatusUpdatedEvent.UserStatus.BLOCKED
    }
}

private fun User.Status.toUserEventStatus(): Status {
    return if (this == User.Status.ACTIVE) Status.ACTIVE else Status.BLOCKED
}

private fun toUserEventGender(genderAsInt: Int): Gender {
    return if (genderAsInt == 1) Gender.MALE else Gender.FEMALE
}