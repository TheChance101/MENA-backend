package net.thechance.dukan.exception

import org.springframework.http.HttpStatus

@ErrorCode(code = 1201, status = HttpStatus.BAD_REQUEST)
class InvalidImageFormatException : Exception("Invalid picture format")
@ErrorCode(code = 1202, status = HttpStatus.INTERNAL_SERVER_ERROR)
class ImageUploadFailedException : Exception("Image uploading failed")