package net.thechance.identity.utils

import net.thechance.identity.entity.OtpLog
import java.time.Instant
import java.util.*

fun createOtpLog(
    phoneNumber: String = "+201122334455",
    otp: String = "00000000",
    isVerified: Boolean = false,
    expireAt: Instant = Instant.now().plusSeconds(180L),
    createdAt: Instant = Instant.now(),
    sessionId: UUID = UUID.fromString("1b3ed35d-94b7-45e4-974c-9da921a27d1c")
): OtpLog {
    return OtpLog(
        phoneNumber = phoneNumber,
        otp = otp,
        isVerified = isVerified,
        expireAt = expireAt,
        createdAt = createdAt,
        sessionId = sessionId
    )
}