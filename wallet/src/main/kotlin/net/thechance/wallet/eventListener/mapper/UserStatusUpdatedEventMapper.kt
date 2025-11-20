package net.thechance.wallet.eventListener.mapper

import net.thechance.events.identity.UserStatusUpdatedEvent
import net.thechance.wallet.entity.WalletUser

fun UserStatusUpdatedEvent.UserStatus.toEntityStatus(): WalletUser.Status {
    return when (this) {
        UserStatusUpdatedEvent.UserStatus.ACTIVE -> WalletUser.Status.ACTIVE
        UserStatusUpdatedEvent.UserStatus.BLOCKED -> WalletUser.Status.BLOCKED
    }
}