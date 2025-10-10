package net.thechance.identity.api.controller

import net.thechance.identity.api.dto.ErrorResponse
import net.thechance.identity.exception.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.core.annotation.Order
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice(assignableTypes = [AddressController::class])
@Order(1)
class AddressControllerAdvice {
    private val logger: Logger = LoggerFactory.getLogger(AddressController::class.java)

    @ExceptionHandler(MethodArgumentNotValidException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleValidationExceptions(exception: MethodArgumentNotValidException): ResponseEntity<Map<String, String?>> {
        val errors = exception.bindingResult.fieldErrors.associate {
            it.field to it.defaultMessage
        }
        logger.error("Validation failed: $errors", exception)
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(errors)
    }

    @ExceptionHandler(AddressNotFoundException::class)
    fun handleAddressNotFoundException(exception: AddressNotFoundException): ResponseEntity<ErrorResponse?> {
        logger.error(exception.message)
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(ErrorResponse(exception.message ?: "Address Not Found"))
    }

    @ExceptionHandler(AddressCanNotBeDeletedException::class)
    fun handleAddressCanNotBeDeletedException(exception: AddressCanNotBeDeletedException): ResponseEntity<ErrorResponse?> {
        logger.error(exception.message)
        return ResponseEntity
            .status(HttpStatus.FORBIDDEN)
            .body(ErrorResponse(exception.message ?: "Address Can Not Be Deleted"))
    }

    @ExceptionHandler(AddressNotAddedException::class)
    fun handleAddressNotAddedException(exception: AddressNotAddedException): ResponseEntity<ErrorResponse?> {
        logger.error(exception.message)
        return ResponseEntity
            .status(HttpStatus.NOT_MODIFIED)
            .body(ErrorResponse(exception.message ?: "Address Not Added"))
    }

    @ExceptionHandler(AddressNotUpdatedException::class)
    fun handleAddressNotUpdatedException(exception: AddressNotUpdatedException): ResponseEntity<ErrorResponse?> {
        logger.error(exception.message)
        return ResponseEntity
            .status(HttpStatus.NOT_MODIFIED)
            .body(ErrorResponse(exception.message ?: "Address Not Updated"))
    }

    @ExceptionHandler(AddressNotDeletedException::class)
    fun handleAddressNotDeletedException(exception: AddressNotDeletedException): ResponseEntity<ErrorResponse?> {
        logger.error(exception.message)
        return ResponseEntity
            .status(HttpStatus.NOT_MODIFIED)
            .body(ErrorResponse(exception.message ?: "Address Not Deleted"))
    }

    @ExceptionHandler(AtLeastAddressValueNeededException::class)
    fun handleAtLeastAddressValueNeededException(exception: AtLeastAddressValueNeededException): ResponseEntity<ErrorResponse?> {
        logger.error(exception.message)
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(ErrorResponse(exception.message ?: "At least one value needed to update"))
    }

    @ExceptionHandler(AddressCanNotBeUpdatedException::class)
    fun handleAddressCanNotBeUpdatedException(exception: AddressCanNotBeUpdatedException): ResponseEntity<ErrorResponse?> {
        logger.error(exception.message)
        return ResponseEntity
            .status(HttpStatus.FORBIDDEN)
            .body(ErrorResponse(exception.message ?: "Address Can Not Be Updated"))
    }
}