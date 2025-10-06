package net.thechance.wallet.service

import net.thechance.wallet.repository.TransactionRepository
import net.thechance.wallet.repository.WalletUserRepository
import net.thechance.wallet.service.helper.PaymentAmountValidationResult
import org.springframework.stereotype.Service
import java.util.*

@Service
class WalletService(
    private val transactionRepository: TransactionRepository,
    private val walletUserRepository: WalletUserRepository
) {

    fun getUserBalance(userId: UUID): Double {

        val totalReceived = transactionRepository.sumAmountByReceiverId(userId) ?: 0.0

        val totalSent = transactionRepository.sumAmountBySenderId(userId) ?: 0.0

        return totalReceived - totalSent
    }

    fun validatePaymentAmount(userId: UUID, amount: Double, recipientId: UUID): PaymentAmountValidationResult {
        val recipient = walletUserRepository.findById(recipientId)
            .orElseThrow { IllegalArgumentException("Recipient not found") }

        val currentBalance = getUserBalance(userId)
        val isValid = amount > 0 && amount <= currentBalance
        return PaymentAmountValidationResult(
            isValid = isValid,
            currentBalance = currentBalance,
            recipientName = recipient.firstName + " " + recipient.lastName,
            recipientImageUrl = recipient.imageUrl
        )
    }
}