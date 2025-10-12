package net.thechance.chat.api.dto

import net.thechance.chat.service.model.ContactModel
import java.util.UUID

data class ContactResponse(
    val id: UUID,
    val firstName: String,
    val lastName: String,
    val phoneNumber: String,
    val menaUserId: UUID?,
    val imageUrl: String?
)

fun ContactModel.toResponse() : ContactResponse {
    return ContactResponse(
        id = this.id,
        firstName = this.firstName,
        lastName = this.lastName,
        phoneNumber = this.phoneNumber,
        menaUserId = this.menaUserId,
        imageUrl = this.imageUrl
    )
}
