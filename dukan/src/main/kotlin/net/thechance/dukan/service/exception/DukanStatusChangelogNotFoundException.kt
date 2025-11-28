package net.thechance.dukan.service.exception

import net.thechance.dukan.api.utils.ErrorCodes.DUKAN_STATUS_CHANGELOG_NOT_FOUND
import org.springframework.http.HttpStatus

class DukanStatusChangelogNotFoundException() : DukanException(
    code = DUKAN_STATUS_CHANGELOG_NOT_FOUND,
    status = HttpStatus.NOT_FOUND,
    message = "Status changelog not found for dukan"
)
