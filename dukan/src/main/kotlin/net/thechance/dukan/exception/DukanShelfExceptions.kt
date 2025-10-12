package net.thechance.dukan.exception

import net.thechance.dukan.exception.ErrorCodes.SHELF_DELETION_NOT_ALLOWED
import net.thechance.dukan.exception.ErrorCodes.SHELF_NAME_ALREADY_TAKEN
import net.thechance.dukan.exception.ErrorCodes.SHELF_NOT_FOUND
import org.springframework.http.HttpStatus


class ShelfDeletionNotAllowedException() : DukanException(
    code = SHELF_DELETION_NOT_ALLOWED,
    status = HttpStatus.CONFLICT,
    message = "Shelf contains products and cannot be deleted"
)

class ShelfNotFoundException() : DukanException(
    code = SHELF_NOT_FOUND,
    status = HttpStatus.NOT_FOUND,
    message = "Shelf not found"
)

class ShelfNameAlreadyTakenException : DukanException(
    code = SHELF_NAME_ALREADY_TAKEN,
    status = HttpStatus.CONFLICT,
    message = "Shelf name already taken"
)
