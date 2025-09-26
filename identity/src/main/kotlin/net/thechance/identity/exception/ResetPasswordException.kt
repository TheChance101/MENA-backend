package net.thechance.identity.exception

abstract class ResetPasswordException(message: String): Exception(message)
class PasswordMismatchException(message: String = "Password and Confirm Password do not match"): ResetPasswordException(message)
class PasswordNotUpdatedException(message: String = "Password not updated"): ResetPasswordException(message)