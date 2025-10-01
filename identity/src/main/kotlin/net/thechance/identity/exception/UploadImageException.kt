package net.thechance.identity.exception

abstract class UploadImageException(message: String): Exception(message)

class InvalidImageException(
    extension: String
): UploadImageException("uploading image with invalid extension:  $extension")

class UnknownErrorException(message: String): UploadImageException(message)