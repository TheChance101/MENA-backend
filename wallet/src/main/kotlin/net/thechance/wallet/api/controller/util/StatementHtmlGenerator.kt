package net.thechance.wallet.api.controller.util

import net.thechance.wallet.entity.Transaction
import net.thechance.wallet.service.model.output.StatementData
import net.thechance.wallet.service.utils.toClientZone
import org.springframework.data.domain.Page
import org.springframework.stereotype.Component
import org.thymeleaf.TemplateEngine
import org.thymeleaf.context.Context
import java.math.BigDecimal
import java.text.DecimalFormat
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

@Component
class StatementHtmlGenerator(
    private val templateEngine: TemplateEngine
) {

    fun generateForPage(
        statementData: StatementData,
        timezone: ZoneId,
        transactionsPage: Page<Transaction>,
    ): String {
        val formattedTransactions = transactionsPage.content.map {
            formatTransaction(it, statementData.userId, timezone)
        }

        val templateData = mapOf(
            "userName" to statementData.username,
            "openingBalance" to formatBalance(statementData.openingBalance),
            "closingBalance" to formatBalance(statementData.closingBalance),
            "transactions" to formattedTransactions,
            "isFirstPage" to (transactionsPage.pageable.pageNumber == 0),
            "isLastPage" to (transactionsPage.pageable.pageNumber == transactionsPage.totalPages - 1)
        )

        val context = Context()
        context.setVariables(templateData)

        return templateEngine.process("statement", context)
    }

    private fun formatTransaction(transaction: Transaction, currentUserId: UUID, timezone: ZoneId): Map<String, Any> {
        return mapOf(
            "id" to "TX-" + transaction.id.toString().substring(0, 6),
            "date" to transaction.createdAt.formatRowItemDate(timezone),
            "time" to transaction.createdAt.formatRowItemTime(timezone),
            "typeHeader" to getTypeHeader(transaction, currentUserId),
            "counterParty" to getCounterParty(transaction, currentUserId),
            "amount" to formatTransactionAmount(currentUserId, transaction),
            "amountValue" to getAmountValue(currentUserId, transaction)
        )
    }

    private fun getTypeHeader(transaction: Transaction, currentUserId: UUID): String {
        return when {
            currentUserId == transaction.receiver.userId -> "Received from"
            transaction.type == Transaction.Type.P2P -> "Sent to"
            transaction.type == Transaction.Type.ONLINE_PURCHASE -> "Purchase from"
            else -> ""
        }
    }

    private fun getCounterParty(transaction: Transaction, currentUserId: UUID): String {
        return when {
            transaction.type == Transaction.Type.DEPOSIT -> "MENA"
            currentUserId == transaction.receiver.userId -> transaction.sender.userName
            transaction.type == Transaction.Type.P2P -> transaction.receiver.userName
            transaction.type == Transaction.Type.ONLINE_PURCHASE -> transaction.receiver.dukan?.name ?: ""
            else -> ""
        }
    }

    private fun formatBalance(amount: Double): String {
        val formatter = DecimalFormat("#,###.##")
        val absAmount = kotlin.math.abs(amount)
        val formatted = formatter.format(absAmount)

        val sign = if (amount < 0) "- " else ""
        return "$sign$formatted"
    }

    private fun formatTransactionAmount(currentUserId: UUID, transaction: Transaction): String {
        val isSender = currentUserId == transaction.sender.userId
        val sign = if (isSender) "-" else "+"

        val formatter = DecimalFormat("#,###.##")
        val formattedAmount = formatter.format(transaction.amount)

        return "$sign $formattedAmount"
    }

    private fun getAmountValue(currentUserId: UUID, transaction: Transaction): BigDecimal {
        return if (currentUserId == transaction.sender.userId) {
            transaction.amount.unaryMinus()
        } else {
            transaction.amount
        }
    }

    private fun LocalDateTime.formatRowItemDate(timezone: ZoneId): String =
        this.toClientZone(timezone)
            .format(DateTimeFormatter.ofPattern("yyyy/MM/dd"))

    private fun LocalDateTime.formatRowItemTime(timezone: ZoneId): String =
        this.toClientZone(timezone)
            .format(DateTimeFormatter.ofPattern("hh:mm a"))

}