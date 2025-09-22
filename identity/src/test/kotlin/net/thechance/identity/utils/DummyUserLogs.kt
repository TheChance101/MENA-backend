package net.thechance.identity.utils

import net.thechance.identity.entity.LoginLog
import java.time.Instant

object DummyUserLogs {
    private val currentTime = Instant.now()

    val loginLogForSuccessLogin = LoginLog(
        user = DummyUsers.validUser1,
        isSuccess = true,
        ipAddress = DummyIpAddresses.validIpAddress2,
        loginTime = currentTime
    )

    val loginLogForFailedLogin = LoginLog(
        user = DummyUsers.userWithInvalidPasswordLength,
        isSuccess = false,
        ipAddress = DummyIpAddresses.validIpAddress3,
        loginTime = currentTime.minusSeconds(10)
    )


    val loginLogsForBlockedUser = listOf(
        LoginLog(
            user = DummyUsers.userWithInvalidPasswordLength,
            isSuccess = false,
            ipAddress = DummyIpAddresses.validIpAddress1,
            loginTime = currentTime
        ),
        LoginLog(
            user = DummyUsers.userWithInvalidPasswordLength,
            isSuccess = false,
            ipAddress = DummyIpAddresses.validIpAddress1,
            loginTime = currentTime.minusSeconds(10)
        ),
        LoginLog(
            user = DummyUsers.userWithInvalidPasswordLength,
            isSuccess = false,
            ipAddress = DummyIpAddresses.validIpAddress1,
            loginTime = currentTime.minusSeconds(40)
        ),
        LoginLog(
            user = DummyUsers.userWithInvalidPasswordLength,
            isSuccess = false,
            ipAddress = DummyIpAddresses.validIpAddress1,
            loginTime = currentTime.minusSeconds(70)
        ),
        LoginLog(
            user = DummyUsers.userWithInvalidPasswordLength,
            isSuccess = false,
            ipAddress = DummyIpAddresses.validIpAddress1,
            loginTime = currentTime.minusSeconds(100)  // max 120-second difference to be blocked
        )
    )

    val loginLogsForUserAfterBlockReleased = listOf(
        LoginLog(
            user = DummyUsers.userWithInvalidPasswordLength,
            isSuccess = false,
            ipAddress = DummyIpAddresses.validIpAddress1,
            loginTime = currentTime.plusSeconds(901) // block released after 15 minutes
        ),
        LoginLog(
            user = DummyUsers.userWithInvalidPasswordLength,
            isSuccess = false,
            ipAddress = DummyIpAddresses.validIpAddress1,
            loginTime = currentTime
        ),
        LoginLog(
            user = DummyUsers.userWithInvalidPasswordLength,
            isSuccess = false,
            ipAddress = DummyIpAddresses.validIpAddress1,
            loginTime = currentTime.minusSeconds(10)
        ),
        LoginLog(
            user = DummyUsers.userWithInvalidPasswordLength,
            isSuccess = false,
            ipAddress = DummyIpAddresses.validIpAddress1,
            loginTime = currentTime.minusSeconds(40)
        ),
        LoginLog(
            user = DummyUsers.userWithInvalidPasswordLength,
            isSuccess = false,
            ipAddress = DummyIpAddresses.validIpAddress1,
            loginTime = currentTime.minusSeconds(70)
        ),
        LoginLog(
            user = DummyUsers.userWithInvalidPasswordLength,
            isSuccess = false,
            ipAddress = DummyIpAddresses.validIpAddress1,
            loginTime = currentTime.minusSeconds(100)
        )
    )
}