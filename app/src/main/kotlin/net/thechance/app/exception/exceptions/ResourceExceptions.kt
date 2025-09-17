package net.thechance.app.exception.exceptions

class ResourceNotFoundException(
    message: String = "The requested resource was not found"
) : RuntimeException(message)

class ResourceConflictException(
    message: String = "The resource already exists or conflicts with existing data"
) : RuntimeException(message)

class AccessForbiddenException(
    message: String = "You do not have permission to access this resource"
) : RuntimeException(message)