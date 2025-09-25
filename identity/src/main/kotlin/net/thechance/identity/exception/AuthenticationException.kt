package net.thechance.identity.exception

open class AuthenticationException(message: String) : Exception(message)
class UserIsBlockedException(message: String) : AuthenticationException(message)
class InvalidIpException(message: String) : AuthenticationException(message)
class InvalidCredentialsException(message: String) : AuthenticationException(message)
class InvalidRefreshTokenException : AuthenticationException("Refresh token is invalid or expired")

open class ResetPasswordException(message: String): Exception(message)
class PasswordMismatchException(message: String = "Password and Confirm Password do not match"): ResetPasswordException(message)
class PasswordNotUpdatedException(message: String = "Password not updated"): ResetPasswordException(message)