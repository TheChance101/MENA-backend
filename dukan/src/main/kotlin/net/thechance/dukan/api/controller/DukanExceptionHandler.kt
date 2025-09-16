package net.thechance.dukan.api.controller

import net.thechance.dukan.exception.DukanCreationFailedException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class DukanExceptionHandler {

    @ExceptionHandler(DukanCreationFailedException::class)
    fun handleDukanCreationException(ex: DukanCreationFailedException): ResponseEntity<String> {
        //TODO: once other team find a way to handle exceptions globally we will do the same
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.message)
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationException(ex: MethodArgumentNotValidException): ResponseEntity<String> {
        //TODO: once other team find a way to handle exceptions globally we will do the same
        val defaultMessage = ex.bindingResult.fieldError?.defaultMessage
        return ResponseEntity(defaultMessage, HttpStatus.BAD_REQUEST)
    }
}