package net.thechance.events.dukan

import net.thechance.events.MenaEvent
import java.util.UUID

data class DukanCreationEvent(
    val id: UUID,
    val ownerId: UUID,
    val name: String,
    val status: Status = Status.PENDING,
    val imageUrl: String?
) : MenaEvent {
    enum class Status {
        APPROVED,
        REJECTED,
        PENDING,
    }
}