package net.thechance.dukan.service.exception

import net.thechance.dukan.api.dto.ErrorResponse
import net.thechance.dukan.entity.Dukan
import org.springframework.core.annotation.Order
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice(basePackages = ["net.thechance.dukan.api.controller"])
@Order(1)
class DukanExceptionHandler {

    @ExceptionHandler(Throwable::class)
    fun handleUnexpectedExceptions(ex: Throwable): ResponseEntity<ErrorResponse> {
        val httpStatus:HttpStatus
        val response = when{
            isDukanShelfConstraintException(ex)-> {
                httpStatus = HttpStatus.CONFLICT
                ErrorResponse(
                    message = ShelfNameAlreadyTakenException().message,
                    errorCode = ShelfNameAlreadyTakenException().code
                )
            }
            else-> {
                httpStatus = HttpStatus.INTERNAL_SERVER_ERROR
                ErrorResponse(
                    message = "Internal server error",
                    errorCode = 500
                )
            }
        }
        return ResponseEntity(response, httpStatus)
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationException(ex: MethodArgumentNotValidException): ResponseEntity<ErrorResponse> {
        val errors = ex.bindingResult.fieldErrors.associate { fieldError ->
            val key = fieldError.field
            key to (fieldError.defaultMessage ?: "validation invalid field")
        }
        val response = ErrorResponse(
            message = "validation failed",
            errors = errors
        )
        return ResponseEntity(response, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(DukanException::class)
    fun handleDukanException(dukanException: DukanException): ResponseEntity<ErrorResponse> {
        val response = ErrorResponse(
            message = dukanException.message,
            errorCode = dukanException.code
        )
        return ResponseEntity(response, dukanException.status)
    }

    private fun isDukanShelfConstraintException(ex: Throwable): Boolean {
        if (ex is DataIntegrityViolationException) {
            val message = ex.mostSpecificCause?.message ?: ex.message.orEmpty()
            return message.contains("uq_dukan_shelves_dukan_title", ignoreCase = true) ||
                    message.contains("ukcdtmkqm7gicyuoymhjywg7w9m", ignoreCase = true)
        }
        return false
    }

}