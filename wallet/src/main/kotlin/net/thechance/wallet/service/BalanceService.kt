package net.thechance.wallet.service

import net.thechance.wallet.api.dto.balance.DepositRequest
import net.thechance.wallet.api.dto.balance.toTransaction
import net.thechance.wallet.exception.BlockedWalletUserException
import net.thechance.wallet.repository.TransactionRepository
import net.thechance.wallet.repository.WalletUserRepository
import net.thechance.wallet.service.utils.orZero
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.*

@Service
class BalanceService(
    private val transactionRepository: TransactionRepository,
    private val walletUserRepository: WalletUserRepository,
    private val blockService: BlockService
) {

    fun getUserBalance(userId: UUID, startDate: LocalDateTime? = null, endDate: LocalDateTime? = null): Double {
        val totalReceived = transactionRepository.sumAmountByReceiverId(userId, startDate, endDate).orZero()
        val totalSent = transactionRepository.sumAmountBySenderId(userId, startDate, endDate).orZero()
        return totalReceived - totalSent
    }

    fun deposit(userId: UUID, request: DepositRequest) {
        val block = blockService.getCurrentBlock()
        val sender = walletUserRepository.getReferenceById(userId)

        val receiver = walletUserRepository.findByPhoneNumber(request.phoneNumber)
            ?: throw IllegalArgumentException("Receiver not found")
        if (receiver.isBlocked())
            throw BlockedWalletUserException("Receiver is blocked")

        val transaction = request.toTransaction(
            sender = sender,
            receiver = receiver,
            block = block
        )
        transactionRepository.save(transaction)
    }
}