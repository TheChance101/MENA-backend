package net.thechance.wallet.service

import net.thechance.wallet.api.dto.transaction.UserTransactionType
import net.thechance.wallet.entity.Transaction
import net.thechance.wallet.exception.NoTransactionsFoundException
import net.thechance.wallet.repository.TransactionRepository
import net.thechance.wallet.service.helper.StatementData
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
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
        val openingBalance = if (startDate == null) 0.0 else transactionRepository.sumUserTransactions(
            status = status,
            transactionTypes = types?.map { it.name },
            startDate = LocalDate.ofEpochDay(0L).atStartOfDay(),
            endDate = startDateTime,
            currentUserId = userId
        ) ?: 0.0

        val closingBalance = transactionRepository.sumUserTransactions(
            status = status,
            transactionTypes = types?.map { it.name },
            startDate = startDateTime,
            endDate = endDateTime,
            currentUserId = userId
        ) ?: 0.0

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