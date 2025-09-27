package net.thechance.wallet.service

import jakarta.persistence.EntityNotFoundException
import net.thechance.wallet.api.dto.TransactionDetailsResponse
import net.thechance.wallet.repository.TransactionRepository
import net.thechance.wallet.service.util.helperFunctions.toDetailsDto
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.nio.file.AccessDeniedException
import java.util.*

@Service
class WalletService(
    private val transactionRepository: TransactionRepository
) {

    fun getUserBalance(userId: UUID): BigDecimal {

        val totalReceived = transactionRepository.sumAmountByReceiverId(userId) ?: BigDecimal.ZERO

        val totalSent = transactionRepository.sumAmountBySenderId(userId) ?: BigDecimal.ZERO

        return totalReceived - totalSent
    }

    fun getTransactionDetails(transactionId: UUID, currentUserId: UUID): TransactionDetailsResponse {
        val transaction = transactionRepository.findById(transactionId)
            .orElseThrow { EntityNotFoundException("Transaction with ID $transactionId not found") }

        if (transaction.senderId != currentUserId && transaction.receiverId != currentUserId) {
            throw AccessDeniedException("User is not authorized to view this transaction")
        }

        return transaction.toDetailsDto()
    }
}