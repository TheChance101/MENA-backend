package net.thechance.identity.api.dto.profile

data class ProfileResponse(
    val id: String,
    val username: String,
    val firstName: String,
    val lastName: String,
    val imageUrl: String?,
    val birthDate: String,
    val gender: Int,
)