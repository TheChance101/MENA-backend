package net.thechance.chat.api.dto

import net.thechance.chat.entity.Contact
import java.util.UUID

data class ContactResponse(
    val id: UUID,
    val firstName: String,
    val lastName: String,
    val phoneNumber: String,
    val userId: UUID,
    val isMenaUser: Boolean,
    val imageUrl: String
)
