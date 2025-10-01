package net.thechance.wallet.service

import net.thechance.wallet.api.dto.transaction.UserTransactionType
import net.thechance.wallet.entity.Transaction
import net.thechance.wallet.exception.NoTransactionsFoundException
import net.thechance.wallet.repository.TransactionRepository
import net.thechance.wallet.repository.WalletUserRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

@Service
class StatementService(
    private val transactionRepository: TransactionRepository,
    private val walletUserRepository: WalletUserRepository,
) {
    fun getUserName(userId: UUID): String {
        return walletUserRepository.findById(userId)
            .orElseThrow { IllegalArgumentException("User not found") }
            .userName
    }

    fun getOpeningBalance(userId: UUID, startDate: LocalDate?): Double {
        val startDateTime = startDate?.atStartOfDay() ?: return 0.0

        return transactionRepository.sumNetUserTransactions(
            endDate = startDateTime,
            currentUserId = userId
        ) ?: 0.0
    }

    fun getClosingBalance(userId: UUID, endDate: LocalDate?): Double {
        val endDateTime = endDate?.plusDays(1)?.atStartOfDay() ?: LocalDateTime.now()

        return transactionRepository.sumNetUserTransactions(
            endDate = endDateTime,
            currentUserId = userId
        ) ?: 0.0
    }

    fun getTransactionsPage(
        userId: UUID,
        startDateTime: LocalDateTime,
        endDateTime: LocalDateTime,
        types: List<UserTransactionType>?,
        pageNum: Int
    ): Page<Transaction> {

        val transactionsPage = transactionRepository.findFilteredTransactions(
            status = Transaction.Status.SUCCESS,
            transactionTypes = types?.map { it.name },
            startDate = startDateTime,
            endDate = endDateTime,
            pageable = PageRequest.of(pageNum, PAGE_SIZE),
            currentUserId = userId
        )

        if (transactionsPage.content.isEmpty()) {
            throw NoTransactionsFoundException("No transactions found for the specified filters")
        }

        return transactionsPage
    }

    private companion object {
        const val PAGE_SIZE = 100
    }
}