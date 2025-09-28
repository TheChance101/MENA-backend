package net.thechance.events

data class UserCreatedEvent(
    val phoneNumber: String,
    val firstName: String,
    val lastName: String,
)
