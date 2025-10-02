package net.thechance.dukan.exception

import org.springframework.http.HttpStatus

@ErrorCode(code = 1101, status = HttpStatus.BAD_REQUEST)
class DukanCreationFailedException() : Exception("Dukan creation failed")

@ErrorCode(code = 1102, status = HttpStatus.NOT_FOUND)
class DukanNotFoundException() : Exception("Dukan not found")