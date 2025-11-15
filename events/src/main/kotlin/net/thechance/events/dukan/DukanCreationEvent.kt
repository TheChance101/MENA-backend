package net.thechance.events.dukan

import net.thechance.events.MenaEvent
import java.util.UUID

data class DukanCreationEvent(
    val id: UUID,
    val ownerId: UUID,
    val name: String,
    val imageUrl: String?,
    val lat: Double,
    val lng: Double
) : MenaEvent