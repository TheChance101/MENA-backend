package net.thechance.dukan.exception

import org.springframework.http.HttpStatus
import kotlin.reflect.KClass


object ExceptionErrorCodes {

    internal var businessErrorMap: Map<KClass<out Exception>, Pair<Int, HttpStatus>> = mapOf(
        DukanNotFoundException::class to (1001 to HttpStatus.NOT_FOUND),
        ShelfNotFoundException::class to (1002 to HttpStatus.NOT_FOUND),
        DukanCreationFailedException::class to (1101 to HttpStatus.BAD_REQUEST)
    )
        private set

}