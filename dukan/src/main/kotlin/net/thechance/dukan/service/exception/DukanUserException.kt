package net.thechance.dukan.service.exception

import net.thechance.dukan.api.utils.ErrorCodes
import org.springframework.http.HttpStatus

class DukanUserNotFoundException(): DukanException(
    code = ErrorCodes.USER_NOT_FOUND,
    status = HttpStatus.NOT_FOUND,
    message = "User Not Found"
)