package net.thechance.dukan.service.mapper

import net.thechance.dukan.entity.Dukan
import net.thechance.events.dukan.DukanStatusChangedEvent

fun Dukan.toDukanStatusChangedEvent(): DukanStatusChangedEvent {
    return DukanStatusChangedEvent(
        dukanId = this.id,
        status = this.status.toEventStatus(),
        activationStatus = this.activationStatus?.toEventActivationStatus()
    )
}

private fun Dukan.Status.toEventStatus(): DukanStatusChangedEvent.DukanEventStatus = when (this) {
    Dukan.Status.APPROVED -> DukanStatusChangedEvent.DukanEventStatus.APPROVED
    Dukan.Status.REJECTED -> DukanStatusChangedEvent.DukanEventStatus.REJECTED
    Dukan.Status.PENDING -> DukanStatusChangedEvent.DukanEventStatus.PENDING
}

private fun Dukan.ActivationStatus.toEventActivationStatus(): DukanStatusChangedEvent.DukanEventActivationStatus =
    when (this) {
        Dukan.ActivationStatus.ACTIVATED -> DukanStatusChangedEvent.DukanEventActivationStatus.ACTIVATED
        Dukan.ActivationStatus.DEACTIVATED -> DukanStatusChangedEvent.DukanEventActivationStatus.DEACTIVATED
    }