package net.thechance.faith.api.controller.exception

import org.springframework.http.HttpStatus


const val INVALID_IMAGE_FORMAT = 1201
const val IMAGE_UPLOAD_FAILED = 1202


class InvalidImageFormatException : FaithException(
    code = INVALID_IMAGE_FORMAT,
    status = HttpStatus.BAD_REQUEST,
    message = "Invalid picture format"
)

class ImageUploadFailedException : FaithException(
    code = IMAGE_UPLOAD_FAILED,
    status = HttpStatus.INTERNAL_SERVER_ERROR,
    message = "Image uploading failed"
)