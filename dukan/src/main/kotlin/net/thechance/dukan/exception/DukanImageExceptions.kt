package net.thechance.dukan.exception

import net.thechance.dukan.api.dto.ErrorCode
import org.springframework.http.HttpStatus

@ErrorCode(code = 2001, status = HttpStatus.BAD_REQUEST)
class InvalidImageFormatException : Exception("Invalid picture format")
@ErrorCode(code = 2002, status = HttpStatus.INTERNAL_SERVER_ERROR)
class ImageUploadFailedException : Exception("Image uploading failed")