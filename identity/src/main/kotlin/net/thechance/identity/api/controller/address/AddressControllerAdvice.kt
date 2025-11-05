package net.thechance.identity.api.controller.address

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
    fun handleValidationExceptions(exception: MethodArgumentNotValidException): ResponseEntity<ErrorResponse> {
        val errors = exception.bindingResult.fieldErrors.associate {
            it.field to it.defaultMessage
        }
        logger.error("Validation failed: $errors", exception)
        val errorResponse = ErrorResponse("Data not valid")
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(errorResponse)
    }

    @ExceptionHandler(AddressNotFoundException::class)
    fun handleAddressNotFoundException(exception: AddressNotFoundException): ResponseEntity<ErrorResponse> {
        logger.error(exception.message)
        val errorResponse = ErrorResponse(exception.message ?: "Address Not Found")
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(errorResponse)
    }

    @ExceptionHandler(AddressCanNotBeDeletedException::class)
    fun handleAddressCanNotBeDeletedException(exception: AddressCanNotBeDeletedException): ResponseEntity<ErrorResponse> {
        logger.error(exception.message)
        val errorResponse = ErrorResponse(exception.message ?: "Address Can Not Be Deleted")
        return ResponseEntity
            .status(HttpStatus.FORBIDDEN)
            .body(errorResponse)
    }

    @ExceptionHandler(AtLeastAddressValueNeededException::class)
    fun handleAtLeastAddressValueNeededException(exception: AtLeastAddressValueNeededException): ResponseEntity<ErrorResponse> {
        logger.error(exception.message)
        val errorResponse = ErrorResponse(exception.message ?: "At least one value needed to update")
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(errorResponse)
    }

    @ExceptionHandler(AddressCanNotBeUpdatedException::class)
    fun handleAddressCanNotBeUpdatedException(exception: AddressCanNotBeUpdatedException): ResponseEntity<ErrorResponse> {
        logger.error(exception.message)
        val errorResponse = ErrorResponse(exception.message ?: "Address Can Not Be Updated")
        return ResponseEntity
            .status(HttpStatus.FORBIDDEN)
            .body(errorResponse)
    }

    @ExceptionHandler(DataNotValidException::class)
    fun handleDataNotValidException(exception: DataNotValidException): ResponseEntity<ErrorResponse> {
        logger.error(exception.message)
        val errorResponse = ErrorResponse(exception.message ?: "Data not valid")
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(errorResponse)
    }

    @ExceptionHandler(NoActiveAddressException::class)
    fun handleNoActiveAddressException(exception: NoActiveAddressException): ResponseEntity<ErrorResponse> {
        logger.error(exception.message)
        val errorResponse = ErrorResponse(exception.message ?: "No active address")
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(errorResponse)
    }
}