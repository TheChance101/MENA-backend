package net.thechance.dukan.exception

import net.thechance.dukan.api.dto.ErrorCode
import org.springframework.http.HttpStatus

class ShelfDeletionNotAllowedException(
    override val message: String = "Shelf contains products and cannot be deleted"
) : RuntimeException(message)

@ErrorCode(code = 1002, status = HttpStatus.NOT_FOUND)
class ShelfNotFoundException(override val message: String = "Shelf not found") : Exception(message)
class ShelfNameAlreadyTakenException : Exception("Shelf name already taken")