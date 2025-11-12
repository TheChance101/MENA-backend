package net.thechance.dukan.service.exception

import net.thechance.dukan.api.utils.ErrorCodes.DUKAN_PRODUCT_CREATION_FAILED
import net.thechance.dukan.api.utils.ErrorCodes.PRODUCT_NAME_ALREADY_TAKEN
import net.thechance.dukan.api.utils.ErrorCodes.PRODUCT_NOT_FOUND
import net.thechance.dukan.api.utils.ErrorCodes.PRODUCT_OUT_OF_STOCK
import net.thechance.dukan.api.utils.ErrorCodes.PRODUCT_UPDATE_FAILED
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

class ProductOutOfStockException : DukanException(
    code = PRODUCT_OUT_OF_STOCK,
    status = HttpStatus.BAD_REQUEST,
    message = "Product out of stock"
)

class ProductUpdateFailedException : DukanException(
    code = PRODUCT_UPDATE_FAILED,
    status = HttpStatus.BAD_REQUEST,
    message = "Product update failed"
)