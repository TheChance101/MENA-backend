package net.thechance.chat.service.model

import java.util.UUID

data class ContactModel(
    val id: UUID,
    val firstName: String,
    val lastName: String,
    val phoneNumber: String,
    val menaUserId: UUID?,
    val imageUrl: String?
)