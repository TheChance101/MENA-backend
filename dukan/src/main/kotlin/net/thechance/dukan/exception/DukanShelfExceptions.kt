package net.thechance.dukan.exception

class ShelfDeletionNotAllowedException(
    override val message: String = "Shelf contains products and cannot be deleted"
) : RuntimeException(message)

class ShelfNotFoundException(override val message: String = "Shelf not found") : Exception(message)
class ShelfNameAlreadyTakenException : Exception("Shelf name already taken")