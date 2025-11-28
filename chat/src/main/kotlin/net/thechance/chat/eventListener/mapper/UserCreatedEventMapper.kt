package net.thechance.chat.eventListener.mapper

import net.thechance.chat.entity.ContactUser
import net.thechance.events.identity.UserCreatedEvent

fun UserCreatedEvent.toUser(): ContactUser {
    return ContactUser(
        id = id,
        firstName = firstName,
        lastName = lastName,
        phoneNumber = phoneNumber,
        imageUrl = imageUrl,
        isDeleted = false
    )
}

