package net.thechance.dukan.exception

import org.springframework.http.HttpStatus

@ErrorCode(code = 1301, status = HttpStatus.NOT_FOUND)
class ProductNotFoundException() : Exception("Product not found")

@ErrorCode(code = 1302, status = HttpStatus.BAD_REQUEST)
class DukanProductCreationFailedException : Exception("Product creation failed")

@ErrorCode(code = 1303, status = HttpStatus.CONFLICT)
class ProductNameAlreadyTakenException : Exception("Product name already taken")