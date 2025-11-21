package net.thechance.dukan.eventListener

import net.thechance.dukan.entity.Dukan
import net.thechance.events.dukan.DukanSearchEvent


fun DukanSearchEvent.Save.Status.toDukanStatus(): Dukan.Status = when (this) {
    DukanSearchEvent.Save.Status.APPROVED -> Dukan.Status.APPROVED
    DukanSearchEvent.Save.Status.REJECTED -> Dukan.Status.REJECTED
    DukanSearchEvent.Save.Status.PENDING -> Dukan.Status.PENDING
}

fun DukanSearchEvent.Save.ActivationStatus.toDukanActivationStatus():Dukan.ActivationStatus = when(this){
    DukanSearchEvent.Save.ActivationStatus.ACTIVATED -> Dukan.ActivationStatus.ACTIVATED
    DukanSearchEvent.Save.ActivationStatus.DEACTIVATED -> Dukan.ActivationStatus.DEACTIVATED
    DukanSearchEvent.Save.ActivationStatus.ONHOLD -> Dukan.ActivationStatus.ONHOLD
}