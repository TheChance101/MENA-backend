package net.thechance.identity.api.dto

data class AuthResponse(
    val accessToken: String,
    val refreshToken: String
)