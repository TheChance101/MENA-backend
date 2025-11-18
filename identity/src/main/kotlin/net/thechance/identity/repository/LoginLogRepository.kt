package net.thechance.identity.repository

import net.thechance.identity.entity.RequestLog
import org.springframework.data.domain.Limit
import org.springframework.data.domain.Sort
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface LoginLogRepository : JpaRepository<RequestLog, UUID> {
    fun findLoginLogsByIpAddressAndUrl(ipAddress: String, url: String, sort: Sort, limit: Limit): List<RequestLog>
}