package net.thechance.app.exception.exceptions

class RefreshTokenExpiredException(
    message: String = "Refresh token has expired"
) : RuntimeException(message)

class InvalidCredentialsException(
    message: String = "Invalid username or password provided"
) : RuntimeException(message)

class JwtTokenExpiredException(
    message: String = "JWT access token has expired"
) : RuntimeException(message)

class AuthenticationRequiredException(
    message: String = "Authentication is required to access this resource"
) : RuntimeException(message)