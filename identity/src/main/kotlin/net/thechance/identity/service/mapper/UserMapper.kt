package net.thechance.identity.service.mapper

import net.thechance.events.identity.UserCreatedEvent
import net.thechance.events.identity.UserStatusUpdatedEvent
import net.thechance.events.identity.UserUpdatedEvent
import net.thechance.events.identity.utils.Gender
import net.thechance.events.identity.utils.Status
import net.thechance.identity.entity.User

fun User.toUserCreatedEvent(): UserCreatedEvent {
    return UserCreatedEvent(
        phoneNumber = phoneNumber,
        password = password,
        firstName = firstName,
        lastName = lastName,
        username = username,
        imageUrl = imageUrl,
        birthDate = birthDate,
        gender = toUserEventGender(gender),
        status = status.toUserEventStatus()
    )
}

fun User.toUserUpdatedEvent(oldUser: User): UserUpdatedEvent {
    return UserUpdatedEvent(
        password = if (password != oldUser.password) password else null,
        firstName = if (firstName != oldUser.firstName) firstName else null,
        lastName = if (lastName != oldUser.lastName) lastName else null,
        username = if (username != oldUser.username) username else null,
        imageUrl = if (imageUrl != oldUser.imageUrl) imageUrl else null,
        birthDate = if (birthDate != oldUser.birthDate) birthDate else null,
        gender = if (gender != oldUser.gender) toUserEventGender(gender) else null,
        status = if (status != oldUser.status) status.toUserEventStatus() else null,
        phoneNumber = if (phoneNumber != oldUser.phoneNumber) phoneNumber else null,
        changedFields = getChangedFields(this, oldUser)
    )
}

fun createUserUpdatedEventForUpdatePassword(
    password: String
): UserUpdatedEvent {
    return UserUpdatedEvent(
        password = password,
        changedFields = listOf(UserUpdatedEvent.ChangedField.PASSWORD)
    )
}

fun createUserUpdatedEvent(
    status: User.Status
): UserUpdatedEvent {
    return UserUpdatedEvent(
        status = status.toUserEventStatus(),
        changedFields = listOf(UserUpdatedEvent.ChangedField.STATUS)
    )
}

fun createUserUpdatedEventForUpdateImage(
    imageUrl: String?
): UserUpdatedEvent {
    return UserUpdatedEvent(
        imageUrl = imageUrl,
        changedFields = listOf(UserUpdatedEvent.ChangedField.IMAGE_URL)
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

private fun getChangedFields(user: User, oldUser: User): List<UserUpdatedEvent.ChangedField> {
    val changedFields = mutableListOf<UserUpdatedEvent.ChangedField>()
    if (user.password != oldUser.password) changedFields.add(UserUpdatedEvent.ChangedField.PASSWORD)
    if (user.firstName != oldUser.firstName) changedFields.add(UserUpdatedEvent.ChangedField.FIRST_NAME)
    if (user.lastName != oldUser.lastName) changedFields.add(UserUpdatedEvent.ChangedField.LAST_NAME)
    if (user.username != oldUser.username) changedFields.add(UserUpdatedEvent.ChangedField.USERNAME)
    if (user.imageUrl != oldUser.imageUrl) changedFields.add(UserUpdatedEvent.ChangedField.IMAGE_URL)
    if (user.birthDate != oldUser.birthDate) changedFields.add(UserUpdatedEvent.ChangedField.BIRTH_DATE)
    if (user.gender != oldUser.gender) changedFields.add(UserUpdatedEvent.ChangedField.GENDER)
    if (user.status != oldUser.status) changedFields.add(UserUpdatedEvent.ChangedField.STATUS)
    if (user.phoneNumber != oldUser.phoneNumber) changedFields.add(UserUpdatedEvent.ChangedField.PHONE_NUMBER)
    return changedFields
}