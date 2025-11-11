package net.thechance.events.dukan

import net.thechance.events.MenaEvent
import java.util.*

data class DukanStatusChangedEvent(
    val dukanId: UUID,
    val name: String,
    val status: DukanEventStatus,
    val activationStatus: DukanEventActivationStatus?,
    val imageUrl: String?,
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
