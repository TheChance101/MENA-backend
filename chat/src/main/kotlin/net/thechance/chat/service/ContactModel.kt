package net.thechance.chat.service

import net.thechance.chat.api.dto.ContactResponse
import net.thechance.chat.entity.Contact
import java.util.UUID

data class ContactModel(
    val id: UUID,
    val firstName: String,
    val lastName: String,
    val phoneNumber: String,
    val isMenaUser: Boolean,
    val imageUrl: String
)

fun Contact.toModel(isMenaUser: Boolean, imageUrl: String): ContactModel{
    return ContactModel(
        id = this.id,
        firstName = this.firstName,
        lastName = this.lastName,
        phoneNumber = this.phoneNumber,
        isMenaUser = isMenaUser,
        imageUrl = imageUrl
    )
}
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
