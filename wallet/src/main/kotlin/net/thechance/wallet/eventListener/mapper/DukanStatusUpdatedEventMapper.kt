package net.thechance.wallet.eventListener.mapper

import net.thechance.events.dukan.DukanUpdateEvent
import net.thechance.wallet.entity.WalletDukan

fun DukanUpdateEvent.Status.toEntityStatus(): WalletDukan.Status{
    return when(this){
        DukanUpdateEvent.Status.APPROVED -> WalletDukan.Status.APPROVED
        DukanUpdateEvent.Status.PENDING -> WalletDukan.Status.PENDING
        DukanUpdateEvent.Status.REJECTED -> WalletDukan.Status.REJECTED
    }
}

fun DukanUpdateEvent.ActivationStatus.toEntityActivationStatus(): WalletDukan.ActivationStatus{
    return when(this){
        DukanUpdateEvent.ActivationStatus.ACTIVATED -> WalletDukan.ActivationStatus.ACTIVATED
        DukanUpdateEvent.ActivationStatus.DEACTIVATED -> WalletDukan.ActivationStatus.DEACTIVATED
    }
}