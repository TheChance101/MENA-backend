package net.thechance.wallet.service.helper

import net.thechance.wallet.entity.Transaction
import org.springframework.core.io.ResourceLoader
import org.springframework.stereotype.Component
import org.thymeleaf.TemplateEngine
import org.thymeleaf.context.Context
import java.math.BigDecimal
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

@Component
class StatementHtmlGenerator(
    private val templateEngine: TemplateEngine,
    private val resourceLoader: ResourceLoader
) {

    fun generateForPage(
        statementData: StatementData,
        transactions: List<Transaction>,
        pageNum: Int
    ): String {
        val formattedTransactions = transactions.map { 
            formatTransaction(it, statementData.userId) 
        }

        val templateData = mapOf(
            "userName" to statementData.username,
            "fromFormatted" to statementData.startDateTime.formatHeaderDate(),
            "toFormatted" to statementData.endDateTime.formatHeaderDate(),
            "openingBalance" to String.format("%.2f", statementData.openingBalance),
            "closingBalance" to String.format("%.2f", statementData.closingBalance),
            "logoSvgInline" to getAppIconSvg(),
            "transactions" to formattedTransactions,
            "isFirstPage" to (pageNum == 0),
            "isLastPage" to (pageNum == statementData.totalPages - 1)
        )

        val context = Context()
        context.setVariables(templateData)

        return templateEngine.process("statement", context)
    }

    private fun formatTransaction(transaction: Transaction, currentUserId: UUID): Map<String, Any> {
        return mapOf(
            "id" to "TX-" + transaction.id.toString().substring(0, 8),
            "date" to transaction.createdAt.formatRowItemDate(),
            "time" to transaction.createdAt.formatRowItemTime(),
            "typeHeader" to getTypeHeader(transaction, currentUserId),
            "counterParty" to getCounterParty(transaction, currentUserId),
            "amount" to getFormattedAmount(currentUserId, transaction),
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
            currentUserId == transaction.receiver.userId -> transaction.sender.userName
            transaction.type == Transaction.Type.P2P -> transaction.receiver.userName
            transaction.type == Transaction.Type.ONLINE_PURCHASE -> transaction.receiver.dukanName ?: ""
            else -> ""
        }
    }

    private fun getFormattedAmount(currentUserId: UUID, transaction: Transaction): String {
        return if (currentUserId == transaction.sender.userId) {
            "-${String.format("%.2f", transaction.amount)}"
        } else {
            "+${String.format("%.2f", transaction.amount)}"
        }
    }

    private fun getAmountValue(currentUserId: UUID, transaction: Transaction): BigDecimal {
        return if (currentUserId == transaction.sender.userId) {
            transaction.amount.unaryMinus()
        } else {
            transaction.amount
        }
    }

    private fun getAppIconSvg(): String {
        return try {
            resourceLoader.getResource("classpath:static/mena_logo.svg").inputStream.use { inputStream ->
                inputStream.reader(Charsets.UTF_8)
                    .readText()
                    .replace("""<\?xml.*?\?>""".toRegex(), "")
                    .trim()
            }
        } catch (_: Exception) {
            ""
        }
    }

    private fun LocalDateTime.formatHeaderDate(): String =
        this.format(DateTimeFormatter.ofPattern("dd MMM yyyy"))
    
    private fun LocalDateTime.formatRowItemDate(): String = 
        this.format(DateTimeFormatter.ofPattern("yyyy/MM/dd"))
    
    private fun LocalDateTime.formatRowItemTime(): String = 
        this.format(DateTimeFormatter.ofPattern("HH:mm a"))
}