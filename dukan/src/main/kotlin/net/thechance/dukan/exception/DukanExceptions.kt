package net.thechance.dukan.exception

import net.thechance.dukan.api.dto.ErrorCode
import org.springframework.http.HttpStatus

@ErrorCode(code = 1101, status = HttpStatus.BAD_REQUEST)
class DukanCreationFailedException() : Exception("Dukan creation failed")

@ErrorCode(code = 1001, status = HttpStatus.NOT_FOUND)
class DukanNotFoundException() : Exception("Dukan not found")