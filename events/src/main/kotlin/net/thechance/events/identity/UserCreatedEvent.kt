package net.thechance.events.identity

import net.thechance.events.MenaEvent

data class UserCreatedEvent(
    val phoneNumber: String,
    val firstName: String,
    val lastName: String,
): MenaEvent