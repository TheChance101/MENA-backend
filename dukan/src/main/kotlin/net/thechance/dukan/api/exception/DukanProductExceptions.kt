package net.thechance.dukan.api.exception

import net.thechance.dukan.api.constants.ErrorCodes.DUKAN_PRODUCT_CREATION_FAILED
import net.thechance.dukan.api.constants.ErrorCodes.PRODUCT_NAME_ALREADY_TAKEN
import net.thechance.dukan.api.constants.ErrorCodes.PRODUCT_NOT_FOUND
import org.springframework.http.HttpStatus

class ProductNotFoundException() : DukanException(
    code = PRODUCT_NOT_FOUND,
    status = HttpStatus.NOT_FOUND,
    message = "Product not found"
)

class DukanProductCreationFailedException : DukanException(
    code = DUKAN_PRODUCT_CREATION_FAILED,
    status = HttpStatus.BAD_REQUEST,
    message = "Product creation failed"
)

class ProductNameAlreadyTakenException : DukanException(
    code = PRODUCT_NAME_ALREADY_TAKEN,
    status = HttpStatus.CONFLICT,
    message = "Product name already taken"
)