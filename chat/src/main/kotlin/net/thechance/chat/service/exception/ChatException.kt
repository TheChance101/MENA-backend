package net.thechance.chat.service.exception


open class ChatException(override val message: String) : Exception(message)

class NotFoundException(message: String) : ChatException(message = message)

class InvalidTimeFormatException(message: String) : ChatException(message = message)

class MalformedMessageContentException(message: String): ChatException(message = message)

class InvalidPhoneNumberException(message: String) : ChatException(message)

class InvalidImageFormatException(
    message: String = "Invalid picture format; supported formats: jpeg ,jpg, png, webp"
) : ChatException(message = message)

class ImageUploadFailedException(message: String) : ChatException(message = message)

class InvalidAudioFormatException(message: String = "Invalid audio format") : ChatException(message = message)

class AudioUploadFailedException(message: String) :  ChatException(message = message)

class FetchWeatherException(message: String, val causedBy: Exception? = null) : ChatException(message)