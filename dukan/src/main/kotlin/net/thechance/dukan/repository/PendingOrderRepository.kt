package net.thechance.dukan.repository

import net.thechance.dukan.entity.PendingOrder
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface PendingOrderRepository : JpaRepository<PendingOrder, UUID> {
    fun findByTransactionId(transactionId: UUID): PendingOrder?
}