package net.thechance.dukan.exception

class ShelfDeletionNotAllowedException(
    override val message: String = "Shelf contains products and cannot be deleted"
) : RuntimeException(message)