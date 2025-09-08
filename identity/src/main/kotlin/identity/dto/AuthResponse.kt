package net.thechance.identity.dto

data class AuthResponse(
    val accessToken: String,
    val refreshToken: String
)