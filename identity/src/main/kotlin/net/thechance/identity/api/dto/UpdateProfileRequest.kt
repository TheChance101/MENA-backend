package net.thechance.identity.api.dto

data class UpdateProfileRequest (
    val username: String,
    val firstName: String,
    val lastName: String,
    val imageUrl: String?,
    val birthDate: String,
    val gender: Int,
)