package net.thechance.chat.dto

data class ContactResponse(
    val id: String,
    val name: String,
    val phoneNumber: String,
    val imageUrl: String? = null,
    val isMenaMember: Boolean
)
