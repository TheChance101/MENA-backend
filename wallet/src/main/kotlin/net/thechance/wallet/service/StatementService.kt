package net.thechance.wallet.service

import net.thechance.wallet.api.dto.transaction.UserTransactionType
import net.thechance.wallet.entity.Transaction
import net.thechance.wallet.exception.NoTransactionsFoundException
import net.thechance.wallet.repository.TransactionRepository
import net.thechance.wallet.service.helper.StatementData
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

@Service
class StatementService(
    private val transactionRepository: TransactionRepository
) {

    fun prepareStatementData(
        userId: UUID,
        startDate: LocalDate?,
        endDate: LocalDate?,
        types: List<UserTransactionType>?,
        status: Transaction.Status?
    ): StatementData {
        val startDateTime = startDate?.atStartOfDay()
            ?: transactionRepository.findFirstBySender_UserIdOrReceiver_UserIdOrderByCreatedAtAsc(
                userId,
                userId
            )?.createdAt ?: LocalDateTime.now()

        val endDateTime = endDate?.atTime(23, 59, 59) ?: LocalDateTime.now()

        // Get first page to validate and extract metadata
        val firstPage = transactionRepository.findFilteredTransactions(
            status = status,
            transactionTypes = types?.map { it.name },
            startDate = startDateTime,
            endDate = endDateTime,
            pageable = PageRequest.of(0, PAGE_SIZE),
            currentUserId = userId
        )

        if (firstPage.isEmpty) {
            throw NoTransactionsFoundException("No transactions found for the specified filters")
        }

        val username = getUsername(firstPage, userId)
        val openingBalance = if (startDate == null) 0.0 else calculateOpeningBalance(
            userId, status, types, startDateTime
        )

        // Calculate closing balance by iterating through all pages
        val closingBalance = calculateClosingBalance(
            userId = userId,
            status = status,
            types = types,
            startDateTime = startDateTime,
            endDateTime = endDateTime,
            openingBalance = openingBalance,
            totalPages = firstPage.totalPages
        )

        // Create a transaction provider function for pagination
        val transactionProvider: (Int) -> List<Transaction> = { pageNum ->
            transactionRepository.findFilteredTransactions(
                status = status,
                transactionTypes = types?.map { it.name },
                startDate = startDateTime,
                endDate = endDateTime,
                pageable = PageRequest.of(pageNum, PAGE_SIZE),
                currentUserId = userId
            ).content
        }

        return StatementData(
            userId = userId,
            username = username,
            startDateTime = startDateTime,
            endDateTime = endDateTime,
            openingBalance = openingBalance,
            closingBalance = closingBalance,
            totalPages = firstPage.totalPages,
            status = status,
            types = types,
            transactionProvider = transactionProvider
        )
    }

    private fun calculateClosingBalance(
        userId: UUID,
        status: Transaction.Status?,
        types: List<UserTransactionType>?,
        startDateTime: LocalDateTime,
        endDateTime: LocalDateTime,
        openingBalance: Double,
        totalPages: Int
    ): Double {
        var balance = openingBalance

        for (pageNum in 0 until totalPages) {
            val page = transactionRepository.findFilteredTransactions(
                status = status,
                transactionTypes = types?.map { it.name },
                startDate = startDateTime,
                endDate = endDateTime,
                pageable = PageRequest.of(pageNum, PAGE_SIZE),
                currentUserId = userId
            )

            balance += page.content.sumOf { transaction ->
                if (userId == transaction.sender.userId) {
                    transaction.amount.unaryMinus()
                } else {
                    transaction.amount
                }
            }.toDouble()
        }

        return balance
    }

    private fun calculateOpeningBalance(
        userId: UUID,
        status: Transaction.Status?,
        types: List<UserTransactionType>?,
        startDate: LocalDateTime
    ): Double {
        return transactionRepository.findFilteredTransactions(
            currentUserId = userId,
            status = status,
            transactionTypes = types?.map { it.name },
            startDate = LocalDate.ofEpochDay(0L).atStartOfDay(),
            endDate = startDate,
            pageable = Pageable.unpaged()
        ).sumOf {
            if (userId == it.sender.userId) it.amount.unaryMinus() else it.amount
        }.toDouble()
    }

    private fun getUsername(transactions: Page<Transaction>, userId: UUID): String {
        return transactions
            .first { it.sender.userId == userId || it.receiver.userId == userId }
            .let {
                if (it.sender.userId == userId) it.sender.userName else it.receiver.userName
            }
    }

    private companion object {
        const val PAGE_SIZE = 100
    }
}