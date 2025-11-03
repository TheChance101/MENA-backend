package net.thechance.identity.api.dto.auth

data class AuthResponse(
    val accessToken: String,
    val refreshToken: String
)