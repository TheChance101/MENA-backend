package net.thechance.events.dukan

import net.thechance.events.MenaEvent
import java.util.UUID

data class DukanStatusChangedEvent(
    val dukanId:UUID,
    val dukanStatus:Status
) : MenaEvent {
    enum class Status {
        APPROVED,
        REJECTED,
        PENDING
    }
}