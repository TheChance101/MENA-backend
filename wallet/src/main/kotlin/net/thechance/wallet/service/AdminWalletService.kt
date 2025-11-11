package net.thechance.wallet.service

import net.thechance.wallet.api.dto.balance.DepositRequest
import net.thechance.wallet.api.dto.balance.toTransaction
import net.thechance.wallet.exception.BlockedWalletUserException
import net.thechance.wallet.repository.TransactionRepository
import net.thechance.wallet.repository.WalletUserRepository
import java.util.*

class AdminWalletService(
    private val transactionRepository: TransactionRepository,
    private val walletUserRepository: WalletUserRepository,
    private val blockService: BlockService
) {

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