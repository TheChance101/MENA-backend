package net.thechance.wallet.eventListener.mapper

import net.thechance.events.dukan.DukanCreationEvent
import net.thechance.wallet.entity.WalletDukan

fun DukanCreationEvent.toEntityDukan(): WalletDukan {
    return WalletDukan(
        dukanId = id,
        name = name,
        imageUrl = imageUrl,
        status = status.toDukanStatus(),
        activationStatus = activationStatus.toDukanActivationStatus()
    )
}

fun DukanCreationEvent.Status.toDukanStatus() = when (this) {
    DukanCreationEvent.Status.APPROVED -> WalletDukan.Status.APPROVED
    DukanCreationEvent.Status.PENDING -> WalletDukan.Status.PENDING
    DukanCreationEvent.Status.REJECTED -> WalletDukan.Status.REJECTED
}

fun DukanCreationEvent.ActivationStatus.toDukanActivationStatus() = when (this) {
    DukanCreationEvent.ActivationStatus.ACTIVATED -> WalletDukan.ActivationStatus.ACTIVATED
    DukanCreationEvent.ActivationStatus.DEACTIVATED -> WalletDukan.ActivationStatus.DEACTIVATED
    DukanCreationEvent.ActivationStatus.ONHOLD -> WalletDukan.ActivationStatus.DEACTIVATED
}


