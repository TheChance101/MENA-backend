package net.thechance.chat.service.model

import net.thechance.chat.api.dto.ContactResponse
import java.util.UUID

data class ContactModel(
    val id: UUID,
    val firstName: String,
    val lastName: String,
    val phoneNumber: String,
    val isMenaUser: Boolean,
    val imageUrl: String
)

fun ContactModel.toResponse() : ContactResponse{
    return ContactResponse(
        id = this.id,
        firstName = this.firstName,
        lastName = this.lastName,
        phoneNumber = this.phoneNumber,
        isMenaUser = this.isMenaUser,
        imageUrl = this.imageUrl
    )
}
