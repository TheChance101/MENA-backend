package net.thechance.events.identity

import net.thechance.events.MenaEvent
import java.util.*

data class UserStatusUpdatedEvent(
    val userId: UUID,
    val status: UserStatus
) : MenaEvent {
    enum class UserStatus {
        ACTIVE,
        BLOCKED
    }
}
