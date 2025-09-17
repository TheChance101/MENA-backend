package net.thechance.app.exception.exceptions

class RequestValidationException(
    message: String = "Request validation failed",
    val validationErrors: List<String>? = null
) : RuntimeException(message)

class MissingRequiredFieldException(
    fieldName: String
) : RuntimeException("Required field '$fieldName' is missing")