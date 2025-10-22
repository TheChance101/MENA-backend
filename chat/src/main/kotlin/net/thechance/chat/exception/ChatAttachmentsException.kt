package net.thechance.chat.exception

import net.thechance.chat.exception.ErrorCodes.IMAGE_UPLOAD_FAILED
import net.thechance.chat.exception.ErrorCodes.INVALID_IMAGE_FORMAT
import org.springframework.http.HttpStatus

class InvalidImageFormatException : ChatException(
    code = INVALID_IMAGE_FORMAT,
    status = HttpStatus.BAD_REQUEST,
    message = "Invalid picture format"
)

class ImageUploadFailedException : ChatException(
    code = IMAGE_UPLOAD_FAILED,
    status = HttpStatus.INTERNAL_SERVER_ERROR,
    message = "Image uploading failed"
)