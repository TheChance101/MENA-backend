package net.thechance.events.dukan

import net.thechance.events.MenaEvent
import java.util.*

data class DukanUpdateEvent(
    val dukanId: UUID,
    val name: String,
    val imageUrl: String?,
    val status: Status,
    val address: String,
    val activationStatus: ActivationStatus
) : MenaEvent {

    enum class Status {
        APPROVED,
        REJECTED,
        PENDING
    }

    enum class ActivationStatus {
        ACTIVATED,
        DEACTIVATED,
        ONHOLD
    }
}
