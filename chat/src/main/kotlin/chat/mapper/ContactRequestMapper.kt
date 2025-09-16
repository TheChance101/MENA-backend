package chat.mapper

import chat.api.dto.ContactRequest
import chat.entity.Contact
import java.util.UUID

fun ContactRequest.toContact(ownerUserId: UUID): Contact{
    return Contact(
        firstName = this.firstName,
        lastName = this.lastName,
        phoneNumber = this.phoneNumber,
        userId = ownerUserId,
    )
}