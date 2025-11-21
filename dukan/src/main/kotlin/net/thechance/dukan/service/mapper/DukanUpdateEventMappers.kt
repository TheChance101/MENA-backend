package net.thechance.dukan.service.mapper

import net.thechance.dukan.entity.Dukan
import net.thechance.events.dukan.DukanUpdateEvent

fun Dukan.toDukanUpdateEvent(): DukanUpdateEvent {
    return DukanUpdateEvent(
        dukanId = this.id,
        name = this.name,
        imageUrl = this.imageUrl,
        address = this.address,
        status = this.status.toEventStatus(),
        activationStatus = this.activationStatus.toEventActivationStatus()
    )
}

private fun Dukan.Status.toEventStatus(): DukanUpdateEvent.Status = when (this) {
    Dukan.Status.APPROVED -> DukanUpdateEvent.Status.APPROVED
    Dukan.Status.REJECTED -> DukanUpdateEvent.Status.REJECTED
    Dukan.Status.PENDING -> DukanUpdateEvent.Status.PENDING
}

private fun Dukan.ActivationStatus.toEventActivationStatus(): DukanUpdateEvent.ActivationStatus =
    when (this) {
        Dukan.ActivationStatus.ACTIVATED -> DukanUpdateEvent.ActivationStatus.ACTIVATED
        Dukan.ActivationStatus.DEACTIVATED -> DukanUpdateEvent.ActivationStatus.DEACTIVATED
        Dukan.ActivationStatus.ONHOLD -> DukanUpdateEvent.ActivationStatus.DEACTIVATED

    }