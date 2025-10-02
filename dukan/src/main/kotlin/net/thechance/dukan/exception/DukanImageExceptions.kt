package net.thechance.dukan.exception

import net.thechance.dukan.exception.ErrorCodes.IMAGE_UPLOAD_FAILED
import net.thechance.dukan.exception.ErrorCodes.INVALID_IMAGE_FORMAT
import org.springframework.http.HttpStatus

class InvalidImageFormatException : DukanException(
    code = INVALID_IMAGE_FORMAT,
    status = HttpStatus.BAD_REQUEST,
    message = "Invalid picture format"
)

class ImageUploadFailedException : DukanException(
    code = IMAGE_UPLOAD_FAILED,
    status = HttpStatus.INTERNAL_SERVER_ERROR,
    message = "Image uploading failed"
)