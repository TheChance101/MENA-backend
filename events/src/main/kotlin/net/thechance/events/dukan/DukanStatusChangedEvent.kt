package net.thechance.events.dukan

import net.thechance.events.MenaEvent
import java.util.*

data class DukanStatusChangedEvent(
    val dukanId: UUID,
    val status: Status,
    val activationStatus: ActivationStatus?
) : MenaEvent {

    enum class Status {
        APPROVED,
        REJECTED,
        PENDING
    }

    enum class ActivationStatus {
        ACTIVATED,
        DEACTIVATED
    }
}
