package net.thechance.faith.exception

class AyahBookmarkNotFoundException(
    message: String = "Bookmark not found"
) : RuntimeException(message)

class FailedToGetPrayerTimesException(
    message: String = "failed to get prayer times"
) : RuntimeException(message)

class InvalidDateFormatException(
    message: String = "Invalid date format."
) : RuntimeException(message)

class ReciterNotFoundException(
    message: String = "Reciter not found."
) : RuntimeException(message)

class InvalidRequestParameterException(
    message: String = "Invalid request parameter."
) : RuntimeException(message)

class InvalidImageFormatException(
    message: String = "Invalid picture format"
) : RuntimeException(message)

class ImageUploadFailedException(
    message: String = "Image uploading failed"
) : RuntimeException(message)
