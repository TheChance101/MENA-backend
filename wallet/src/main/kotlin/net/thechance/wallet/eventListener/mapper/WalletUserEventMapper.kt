package net.thechance.wallet.eventListener.mapper

import net.thechance.events.identity.UserCreatedEvent
import net.thechance.events.identity.UserUpdatedEvent
import net.thechance.events.identity.utils.Status
import net.thechance.wallet.entity.WalletUser

fun UserCreatedEvent.toWalletUserEntity(): WalletUser {
    return WalletUser(
        userId = id,
        firstName = firstName,
        lastName = lastName,
        imageUrl = imageUrl,
        phoneNumber = phoneNumber,
        isDeleted = false,
        dukan = null,
        status = status.toEntityStatus()
    )
}

fun UserUpdatedEvent.toWalletUserEntity(): WalletUser {
    return WalletUser(
        userId = id,
        firstName = firstName,
        lastName = lastName,
        imageUrl = imageUrl,
        phoneNumber = phoneNumber,
        isDeleted = false,
        dukan = null,
        status = status.toEntityStatus()
    )
}

private fun Status.toEntityStatus(): WalletUser.Status {
    return when (this) {
        Status.ACTIVE -> WalletUser.Status.ACTIVE
        Status.BLOCKED -> WalletUser.Status.BLOCKED
    }
}


