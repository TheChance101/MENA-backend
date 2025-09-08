package chat.dto


import java.util.UUID

class ContactResponse(
    val id: UUID = UUID.randomUUID(),
    val name: String,
    val phoneNumber: String,
    val userId: UUID,
    val imageUrl: String,
    val isMENAMember: Boolean
)