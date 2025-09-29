package net.thechance.dukan.exception

class ShelfNotFoundException (override val message: String = "Shelf not found"): Exception(message)