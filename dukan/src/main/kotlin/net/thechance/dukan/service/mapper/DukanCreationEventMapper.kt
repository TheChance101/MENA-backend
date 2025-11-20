package net.thechance.dukan.service.mapper

import net.thechance.dukan.entity.Dukan
import net.thechance.events.dukan.DukanCreationEvent

fun Dukan.toDukanCreationEvent(): DukanCreationEvent {
    return DukanCreationEvent(
        id = this.id,
        name = this.name,
        imageUrl = this.imageUrl,
        activationStatus = this.activationStatus?.toEventActivationStatus(),
        status = this.status.toEventStatus(),
        ownerId = this.ownerId,
    )
}

private fun Dukan.Status.toEventStatus(): DukanCreationEvent.Status = when (this) {
    Dukan.Status.APPROVED -> DukanCreationEvent.Status.APPROVED
    Dukan.Status.REJECTED -> DukanCreationEvent.Status.REJECTED
    Dukan.Status.PENDING -> DukanCreationEvent.Status.PENDING
}

private fun Dukan.ActivationStatus.toEventActivationStatus(): DukanCreationEvent.ActivationStatus =
    when (this) {
        Dukan.ActivationStatus.ACTIVATED -> DukanCreationEvent.ActivationStatus.ACTIVATED
        Dukan.ActivationStatus.DEACTIVATED -> DukanCreationEvent.ActivationStatus.DEACTIVATED
    }