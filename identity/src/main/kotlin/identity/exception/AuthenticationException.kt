package net.thechance.identity.exception

open class AuthenticationException(message: String) : Exception(message)
class UserIsBlockedException(message: String) : AuthenticationException(message)