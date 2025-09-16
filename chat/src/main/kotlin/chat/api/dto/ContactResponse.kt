package chat.api.dto

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

fun Contact.toResponse(isMenaUser: Boolean, imageUrl: String ): ContactResponse {
    return ContactResponse(
        id = id,
        firstName = firstName,
        lastName = lastName,
        phoneNumber = phoneNumber,
        userId = userId,
        isMenaUser = isMenaUser,
        imageUrl = imageUrl
    )
}
