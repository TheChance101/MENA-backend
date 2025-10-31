package net.thechance.identity.exception

abstract class RegisterException(message: String): Exception(message)

class UserAlreadyExistsException(message: String = "User already exists"): RegisterException(message)