package net.thechance.dukan.exception

class InvalidPictureException : Exception("Invalid picture format")

class ImageUploadingFailedException : Exception("Image uploading failed")

class ShelfNameAlreadyTakenException : Exception("Shelf name already taken")