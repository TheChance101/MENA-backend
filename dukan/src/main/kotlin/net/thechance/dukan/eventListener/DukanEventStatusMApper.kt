package net.thechance.dukan.eventListener

import net.thechance.dukan.entity.Dukan
import net.thechance.events.dukan.DukanEvent


fun DukanEvent.Save.Status.toDukanStatus():Dukan.Status = when(this){
    DukanEvent.Save.Status.ACTIVATED -> Dukan.Status.ACTIVATED
    DukanEvent.Save.Status.REJECTED -> Dukan.Status.REJECTED
    DukanEvent.Save.Status.PENDING -> Dukan.Status.PENDING
    DukanEvent.Save.Status.DEACTIVATED -> Dukan.Status.DEACTIVATED
}