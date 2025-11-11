package net.thechance.events.dukan

import net.thechance.events.MenaEvent
import java.util.*

data class DukanStatusChangedEvent(
    val dukanId: UUID,
    val status: DukanEventStatus,
    val activationStatus: DukanEventActivationStatus?,
) : MenaEvent {

    enum class DukanEventStatus {
        APPROVED,
        REJECTED,
        PENDING
    }

    enum class DukanEventActivationStatus {
        ACTIVATED,
        DEACTIVATED
    }
}
