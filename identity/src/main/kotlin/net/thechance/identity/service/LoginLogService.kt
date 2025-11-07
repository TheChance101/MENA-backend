package net.thechance.identity.service

import net.thechance.identity.entity.RequestLog
import net.thechance.identity.repository.LoginLogRepository
import org.springframework.data.domain.Limit
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service

@Service
class LoginLogService(
    private val loginLogRepository: LoginLogRepository
) {
    fun addLoginLog(requestLog: RequestLog) {
        loginLogRepository.save(requestLog)
    }

    fun getLoginLogsByIpAddress(
        ipAddress: String,
        url: String,
        numberOfLogs: Int,
        sortedBy: String = RequestLog::loginTime.name,
        sortedDirection: Sort.Direction = Sort.Direction.DESC
    ): List<RequestLog> {
        return loginLogRepository.findLoginLogsByIpAddressAndUrl(
            ipAddress = ipAddress,
            url = url,
            sort = Sort.by(sortedDirection, sortedBy),
            limit = Limit.of(numberOfLogs)
        )
    }
}