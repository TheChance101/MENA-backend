package net.thechance.identity.service

import net.thechance.identity.entity.LoginLog
import net.thechance.identity.repository.LoginLogRepository
import org.springframework.data.domain.Limit
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service

@Service
class LoginLogService(
    private val loginLogRepository: LoginLogRepository
) {
    fun addLoginLog(loginLog: LoginLog) {
        loginLogRepository.save(loginLog)
    }

    fun getLoginLogsByIpAddress(ipAddress: String, numberOfLogs: Int): List<LoginLog> {
        return loginLogRepository.findLoginLogsByIpAddress(
            ipAddress = ipAddress,
            sort = Sort.by(Sort.Direction.DESC, LoginLog::loginTime.name),
            limit = Limit.of(numberOfLogs)
        )
    }
}