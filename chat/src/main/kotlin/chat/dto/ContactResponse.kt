package chat.dto

class ContactResponse(
    val id: String,
    val name: String,
    val phoneNumber: String,
    val userId: String,
    val imageUrl: String,
    val isMenaMember: Boolean
)