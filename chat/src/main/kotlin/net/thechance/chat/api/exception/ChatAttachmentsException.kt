package net.thechance.chat.api.exception

class InvalidImageFormatException : ChatException(
    code = ErrorCodes.INVALID_IMAGE_FORMAT, message = "Invalid picture format; supported formats: jpeg ,jpg, png, webp"
)

class ImageUploadFailedException(message: String) : ChatException(
    code = ErrorCodes.IMAGE_UPLOAD_FAILED, message = message
)