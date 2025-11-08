package net.thechance.wallet.service

import net.thechance.wallet.entity.Transaction
import net.thechance.wallet.exception.NoTransactionsFoundException
import net.thechance.wallet.service.model.input.TransactionFilterParams
import net.thechance.wallet.service.model.input.UserTransactionType
import net.thechance.wallet.service.model.output.StatementData
import net.thechance.wallet.service.utils.atEndOfDay
import net.thechance.wallet.service.utils.orNow
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

@Service
class StatementService(
    private val balanceService: BalanceService,
    private val transactionService: TransactionService,
    private val walletUserService: WalletUserService,
) {
    fun getStatementData(
        userId: UUID,
        types: List<UserTransactionType>?,
        startDate: LocalDate?,
        endDate: LocalDate?
    ): StatementData {
        val startDateTime = getStartDateTime(startDate, userId)
        val endDateTime = getEndDateTime(endDate)

        return StatementData(
            userId = userId,
            username = getUserName(userId),
            types = types,
            startDateTime = startDateTime,
            endDateTime = endDateTime,
            openingBalance = getOpeningBalance(userId, startDateTime),
            closingBalance = getClosingBalance(userId, endDateTime)
        )
    }

    private fun getStartDateTime(startDate: LocalDate?, userId: UUID): LocalDateTime {
        return startDate?.atStartOfDay()
            ?: transactionService.getUserFirstTransactionDate(userId)
                .orNow().toLocalDate().atStartOfDay()
    }

    private fun getEndDateTime(endDate: LocalDate?): LocalDateTime {
        return endDate?.atEndOfDay().orNow()
    }

    private fun getUserName(userId: UUID): String {
        return walletUserService.getUserById(userId).userName
    }

    private fun getOpeningBalance(userId: UUID, startDate: LocalDateTime): Double {
        return balanceService.getUserBalance(
            userId = userId,
            endDate = startDate
        )
    }

    private fun getClosingBalance(userId: UUID, endDate: LocalDateTime): Double {
        return balanceService.getUserBalance(
            userId = userId,
            endDate = endDate
        )
    }

    fun getTransactionsPage(
        userId: UUID,
        startDateTime: LocalDateTime,
        endDateTime: LocalDateTime,
        types: List<UserTransactionType>?,
        pageNum: Int
    ): Page<Transaction> {
        return transactionService.getFilteredTransactions(
            transactionFilterParams = TransactionFilterParams(
                status = Transaction.Status.SUCCESS,
                types = types,
                startDate = startDateTime.toLocalDate(),
                endDate = endDateTime.toLocalDate()
            ),
            currentUserId = userId,
            pageable = PageRequest.of(
                pageNum,
                PAGE_SIZE,
                Sort.by(Sort.Direction.ASC, Transaction::createdAt.name)
            )
        ).also {
            if(pageNum == 0 && it.isEmpty) throw NoTransactionsFoundException("No transactions found for the specified period.")
        }
    }

    fun getPageInflows(transactions: Page<Transaction>, userId: UUID): BigDecimal {
        return transactions.sumOf { transaction ->
            when {
                transaction.receiver.userId == userId -> transaction.amount
                else -> 0.toBigDecimal()
            }
        }
    }

    fun getPageOutflows(transactions: Page<Transaction>, userId: UUID): BigDecimal {
        return transactions.sumOf { transaction ->
            when {
                transaction.sender.userId == userId -> transaction.amount
                else -> 0.toBigDecimal()
            }
        }
    }

    private companion object {
        const val PAGE_SIZE = 100
    }
}