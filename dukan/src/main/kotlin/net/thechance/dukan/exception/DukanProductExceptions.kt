package net.thechance.dukan.exception

import net.thechance.dukan.api.dto.ErrorCode
import org.springframework.http.HttpStatus

@ErrorCode(code = 3001, status = HttpStatus.NOT_FOUND)
class ProductNotFoundException() : Exception("Product not found")

@ErrorCode(code = 3100, status = HttpStatus.BAD_REQUEST)
class DukanProductCreationFailedException : Exception("Product creation failed")

@ErrorCode(code = 3102, status = HttpStatus.CONFLICT)
class ProductNameAlreadyTakenException : Exception("Product name already taken")