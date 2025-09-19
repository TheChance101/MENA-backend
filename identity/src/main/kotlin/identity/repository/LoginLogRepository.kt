package net.thechance.identity.repository

import net.thechance.identity.entity.LoginLog
import org.springframework.data.domain.Limit
import org.springframework.data.domain.Sort
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface LoginLogRepository : JpaRepository<LoginLog, UUID> {
    fun findLoginLogsByIpAddress(ipAddress: String, sort: Sort, limit: Limit): List<LoginLog>
}