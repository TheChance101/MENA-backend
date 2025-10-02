package net.thechance.dukan.exception

import net.thechance.dukan.exception.ErrorCodes.DUKAN_CREATION_FAILED
import net.thechance.dukan.exception.ErrorCodes.DUKAN_NOT_FOUND
import org.springframework.http.HttpStatus


class DukanCreationFailedException() : DukanException(
    code = DUKAN_CREATION_FAILED,
    status = HttpStatus.BAD_REQUEST,
    message = "Dukan creation failed"
)

class DukanNotFoundException() : DukanException(
    code = DUKAN_NOT_FOUND,
    status = HttpStatus.NOT_FOUND,
    message = "Dukan not found"
)