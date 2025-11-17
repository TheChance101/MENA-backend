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

private fun Dukan.Status.toEventStatus(): DukanStatusChangedEvent.Status = when (this) {
    Dukan.Status.APPROVED -> DukanStatusChangedEvent.Status.APPROVED
    Dukan.Status.REJECTED -> DukanStatusChangedEvent.Status.REJECTED
    Dukan.Status.PENDING -> DukanStatusChangedEvent.Status.PENDING
}

private fun Dukan.ActivationStatus.toEventActivationStatus(): DukanStatusChangedEvent.ActivationStatus =
    when (this) {
        Dukan.ActivationStatus.ACTIVATED -> DukanStatusChangedEvent.ActivationStatus.ACTIVATED
        Dukan.ActivationStatus.DEACTIVATED -> DukanStatusChangedEvent.ActivationStatus.DEACTIVATED
    }