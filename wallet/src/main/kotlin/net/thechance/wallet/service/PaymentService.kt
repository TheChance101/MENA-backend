package net.thechance.wallet.service

import net.thechance.wallet.repository.WalletUserRepository
import net.thechance.wallet.service.helper.PaymentAmountValidationResult
import org.springframework.stereotype.Service
import java.util.*

@Service
class PaymentService(
    private val walletService: WalletService,
    private val walletUserRepository: WalletUserRepository
) {

    fun validatePaymentAmount(userId: UUID, amount: Double, receiverId: UUID): PaymentAmountValidationResult {
        val receiver = walletUserRepository.findById(receiverId)
            .orElseThrow { IllegalArgumentException("Receiver not found") }

        val currentBalance = walletService.getUserBalance(userId)
        val isValid = amount > 0 && amount <= currentBalance
        return PaymentAmountValidationResult(
            isValid = isValid,
            currentBalance = currentBalance,
            receiverName = receiver.firstName + " " + receiver.lastName,
            receiverImageUrl = receiver.imageUrl
        )
    }
}
