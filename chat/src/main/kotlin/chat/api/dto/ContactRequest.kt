package chat.api.dto

data class ContactRequest(
    val firstName: String,
    val lastName: String,
    val phoneNumber: String
)