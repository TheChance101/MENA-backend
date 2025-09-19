package net.thechance.dukan.api.controller

import net.thechance.dukan.exception.DukanNotFoundException
import net.thechance.dukan.exception.ImageUploadingFailedException
import net.thechance.dukan.exception.InvalidPictureException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class DukanExceptionHandler {
    @ExceptionHandler(DukanNotFoundException::class)
    fun handleDukanNotFoundException(ex: DukanNotFoundException): ResponseEntity<String> {
        //TODO: once other team find a way to handle exceptions globally we will do the same
        return ResponseEntity(ex.message, HttpStatus.NOT_FOUND)
    }

    @ExceptionHandler(InvalidPictureException::class)
    fun handleInvalidPictureException(ex: InvalidPictureException): ResponseEntity<String> {
        //TODO: once other team find a way to handle exceptions globally we will do the same
        return ResponseEntity(ex.message, HttpStatus.NOT_FOUND)
    }

    @ExceptionHandler(ImageUploadingFailedException::class)
    fun handleImageUploadingFailedException(ex: ImageUploadingFailedException): ResponseEntity<String> {
        //TODO: once other team find a way to handle exceptions globally we will do the same
        return ResponseEntity(ex.message, HttpStatus.NOT_FOUND)
    }
}