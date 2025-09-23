package net.thechance.chat.api.dto

import java.util.UUID

data class ContactResponse(
    val id: UUID,
    val firstName: String,
    val lastName: String,
    val phoneNumber: String,
    val isMenaUser: Boolean,
    val userId: UUID?,
    val imageUrl: String?
)
