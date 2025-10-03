package net.thechance.identity.exception

abstract class AuthenticationException(message: String) : Exception(message)
class UserIsBlockedException(message: String) : AuthenticationException(message)
class InvalidIpException(message: String) : AuthenticationException(message)
class InvalidCredentialsException(message: String) : AuthenticationException(message)
class UserNotFoundException(message: String) : AuthenticationException(message)
class InvalidRefreshTokenException : AuthenticationException("Refresh token is invalid or expired")