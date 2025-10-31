package net.thechance.chat.service.exception

class InvalidImageFormatException : ChatException(
    message = "Invalid picture format; supported formats: jpeg ,jpg, png, webp"
)

class ImageUploadFailedException(message: String) : ChatException(
    message = message
)