package net.thechance.dukan.exception

import org.springframework.http.HttpStatus

@ErrorCode(code = 1401, status = HttpStatus.CONFLICT)
class ShelfDeletionNotAllowedException(
    override val message: String = "Shelf contains products and cannot be deleted"
) : Exception(message)

@ErrorCode(code = 1402, status = HttpStatus.NOT_FOUND)
class ShelfNotFoundException(override val message: String = "Shelf not found") : Exception(message)

@ErrorCode(code = 1403, status = HttpStatus.CONFLICT)
class ShelfNameAlreadyTakenException : Exception("Shelf name already taken")