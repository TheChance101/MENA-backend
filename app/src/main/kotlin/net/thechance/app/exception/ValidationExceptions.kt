package net.thechance.app.exception

class AccessForbiddenException(
    message: String = "You do not have permission to access this resource"
) : RuntimeException(message)

class RequestValidationException(
    message: String = "Request validation failed"
) : RuntimeException(message)

class MissingRequiredFieldException(
    fieldName: String
) : RuntimeException("Required field '$fieldName' is missing")