package net.thechance.identity.service.mapper

import net.thechance.events.identity.UserStatusUpdatedEvent
import net.thechance.identity.entity.User

fun User.Status.toEventStatus(): UserStatusUpdatedEvent.UserStatus {
    return when (this) {
        User.Status.ACTIVE -> UserStatusUpdatedEvent.UserStatus.ACTIVE
        User.Status.BLOCKED -> UserStatusUpdatedEvent.UserStatus.BLOCKED
    }
}