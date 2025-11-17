package net.thechance.wallet.eventListener.mapper

import net.thechance.events.dukan.DukanCreationEvent
import net.thechance.events.dukan.DukanStatusChangedEvent
import net.thechance.wallet.entity.WalletDukan

fun DukanStatusChangedEvent.Status.toEntityStatus(): WalletDukan.Status{
    return when(this){
        DukanStatusChangedEvent.Status.APPROVED -> WalletDukan.Status.APPROVED
        DukanStatusChangedEvent.Status.PENDING -> WalletDukan.Status.PENDING
        DukanStatusChangedEvent.Status.REJECTED -> WalletDukan.Status.REJECTED
    }
}

fun DukanStatusChangedEvent.ActivationStatus.toEntityActivationStatus(): WalletDukan.ActivationStatus{
    return when(this){
        DukanStatusChangedEvent.ActivationStatus.ACTIVATED -> WalletDukan.ActivationStatus.ACTIVATED
        DukanStatusChangedEvent.ActivationStatus.DEACTIVATED -> WalletDukan.ActivationStatus.DEACTIVATED
    }
}