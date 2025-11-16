package net.thechance.dukan.service.exception

import net.thechance.dukan.api.utils.ErrorCodes.FORBIDDEN_ORDER_ACCESS
import net.thechance.dukan.api.utils.ErrorCodes.ORDER_NOT_FOUND
import org.springframework.http.HttpStatus

class ForbiddenException() : DukanException(
    code = FORBIDDEN_ORDER_ACCESS,
    status = HttpStatus.FORBIDDEN,
    message = "You are not allowed to access this order"
)

class OrderNotFoundException() : DukanException(
    code = ORDER_NOT_FOUND,
    status = HttpStatus.NOT_FOUND,
    message = "Order not found"
)