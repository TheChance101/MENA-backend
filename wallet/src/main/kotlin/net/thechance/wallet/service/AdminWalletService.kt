package net.thechance.wallet.service

import net.thechance.wallet.entity.Transaction
import net.thechance.wallet.exception.BlockedWalletUserException
import net.thechance.wallet.repository.TransactionRepository
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.util.*

@Service
class AdminWalletService(
    private val transactionRepository: TransactionRepository,
    private val walletUserService: WalletUserService,
    private val blockService: BlockService
) {
    fun deposit(userId: UUID, receiverPhoneNumber: String, amount: Double) {
        val block = blockService.getCurrentBlock()
        val sender = walletUserService.getUserById(userId)
        val receiver = walletUserService.getUserByPhoneNumber(receiverPhoneNumber)
        if (receiver.isBlocked())
            throw BlockedWalletUserException("Receiver is blocked")

        val transaction = Transaction(
            id = UUID.randomUUID(),
            sender = sender,
            receiver = receiver,
            amount = BigDecimal.valueOf(amount),
            block = block,
            status = Transaction.Status.SUCCESS,
            type = Transaction.Type.DEPOSIT
        )
        transactionRepository.save(transaction)
    }
}
