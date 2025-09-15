package wallet.repository

import org.springframework.data.jpa.repository.JpaRepository
import wallet.entity.Transaction
import java.util.UUID

interface TransactionRepository:JpaRepository<Transaction,UUID> {
    fun findBySenderIdOrReceiverId(senderId: UUID, receiverId: UUID): List<Transaction>
}