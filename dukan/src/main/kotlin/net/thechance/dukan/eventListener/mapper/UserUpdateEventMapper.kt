package net.thechance.dukan.eventListener.mapper

import net.thechance.dukan.entity.DukanUser
import net.thechance.events.identity.UserUpdatedEvent

fun UserUpdatedEvent.toUser(): DukanUser {
    return DukanUser(
        userId = id,
        firstName = firstName,
        lastName = lastName,
        phoneNumber = phoneNumber,
        imageUrl = imageUrl
    )
}