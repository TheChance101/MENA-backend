package net.thechance.wallet.repository

import net.thechance.wallet.entity.Transaction
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface TransactionRepository:JpaRepository<Transaction,UUID> {
    fun findBySenderAddressOrReceiverAddress(senderAddress: String, receiverAddress: String): List<Transaction>
}