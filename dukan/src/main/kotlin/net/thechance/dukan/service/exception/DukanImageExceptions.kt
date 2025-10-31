package net.thechance.dukan.service.exception

import net.thechance.dukan.api.utils.ErrorCodes.IMAGE_DELETION_FAILED
import net.thechance.dukan.api.utils.ErrorCodes.IMAGE_UPLOAD_FAILED
import net.thechance.dukan.api.utils.ErrorCodes.INVALID_IMAGE_FORMAT
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

class ImageDeleteFailedException : DukanException(
    code = IMAGE_DELETION_FAILED,
    status = HttpStatus.INTERNAL_SERVER_ERROR,
    message = "Image deleting failed"
)