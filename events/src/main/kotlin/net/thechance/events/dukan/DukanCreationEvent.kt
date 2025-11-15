package net.thechance.events.dukan

import net.thechance.events.MenaEvent
import java.util.UUID

data class DukanCreationEvent(
    val id: UUID,
    val ownerId: UUID,
    val name: String,
    val imageUrl: String?,
    val status: Status = Status.PENDING,
    val activationStatus: ActivationStatus? = null
) : MenaEvent {
    enum class Status {
        APPROVED,
        REJECTED,
        PENDING,
    }

    enum class ActivationStatus {
        ACTIVATED,
        DEACTIVATED
    }
}