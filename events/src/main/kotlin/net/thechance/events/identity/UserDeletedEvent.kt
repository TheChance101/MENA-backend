package net.thechance.events.identity

import net.thechance.events.MenaEvent
import java.util.UUID

data class UserDeletedEvent(
    val id: UUID,
): MenaEvent