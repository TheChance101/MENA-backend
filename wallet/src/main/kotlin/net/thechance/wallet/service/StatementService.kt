package net.thechance.wallet.service

import com.itextpdf.html2pdf.HtmlConverter
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.font.FontProvider
import jakarta.servlet.http.HttpServletResponse
import net.thechance.wallet.api.dto.transaction.UserTransactionType
import net.thechance.wallet.entity.Transaction
import net.thechance.wallet.repository.TransactionRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.util.ResourceUtils
import org.thymeleaf.TemplateEngine
import org.thymeleaf.context.Context
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

@Service
class StatementService(
    private val transactionRepository: TransactionRepository,
    private val templateEngine: TemplateEngine
) {

    fun generateStatementPdf(
        userId: UUID,
        startDate: LocalDate?,
        endDate: LocalDate?,
        type: UserTransactionType?,
        status: Transaction.Status?,
        response: HttpServletResponse
    ) {
        val earliestTransactionDate = transactionRepository.findFirstBySender_UserIdOrReceiver_UserIdOrderByCreatedAtAsc(
            userId,
            userId
        )?.createdAt ?: LocalDateTime.now()
        val startDateTime =
            startDate?.atStartOfDay()
                ?: earliestTransactionDate

        val endDateTime = endDate?.atTime(23, 59, 59, 59) ?: LocalDateTime.now()

        try {
            val transactions = transactionRepository.findFilteredTransactions(
                status = status,
                transactionType = type?.name,
                startDate = startDateTime,
                endDate = endDateTime,
                pageable = Pageable.unpaged(),
                currentUserId = userId
            )
            val transactionsCount = transactions.totalElements

            if (transactions.isEmpty) {
                response.status = HttpServletResponse.SC_NO_CONTENT
                response.writer.write("No transactions found for the specified filters")
                return
            }
            val username = getUsername(transactions, userId)

            val openingBalance = getOpeningBalance(userId, status, type, earliestTransactionDate, startDateTime)

            val closingBalance = getClosingBalance(openingBalance, transactions, userId)

            val templateData = mutableMapOf<String?, Any?>(
                "userName" to username,
                "fromFormatted" to startDateTime.formatHeaderDate(),
                "toFormatted" to endDateTime.formatHeaderDate(),
                "openingBalance" to String.format("%.2f", openingBalance),
                "closingBalance" to String.format("%.2f", closingBalance),
                "logoSvgInline" to getAppIconSvg(),
                "transactions" to transactions.map { mapTransactionForTemplate(userId, it) }
            )

            setResponseHeaders(response, startDateTime, endDateTime)

            generatePdfFile(templateData, response)
        } catch (e: Exception) {
            response.status = HttpServletResponse.SC_INTERNAL_SERVER_ERROR
            response.writer.write("Error generating PDF: ${e.message}")
        }
    }

    private fun generatePdfFile(data: Map<String?, Any?>?, response: HttpServletResponse) {
        val context = Context()
        context.setVariables(data)

        val htmlContent = templateEngine.process("statement", context)
        val writer = PdfWriter(response.outputStream)
        val pdf = PdfDocument(writer)

        val converterProperties = com.itextpdf.html2pdf.ConverterProperties()

        val fontProvider = FontProvider()
        listOf(
            "fonts/MadimiOne-Regular.ttf",
            "fonts/Poppins-Regular.ttf",
            "fonts/Poppins-Medium.ttf",
            "fonts/Poppins-SemiBold.ttf"
        ).forEach { fontPath ->
            javaClass.classLoader.getResource(fontPath)?.path?.let { resourcePath ->
                fontProvider.addFont(resourcePath)
            }
        }

        converterProperties.fontProvider = fontProvider
        converterProperties.isImmediateFlush = true

        HtmlConverter.convertToPdf(htmlContent, pdf, converterProperties)

        pdf.close()
    }

    private fun getUsername(
        transactions: Page<Transaction>,
        userId: UUID
    ): String = transactions
        .first { it.sender.userId == userId || it.receiver.userId == userId }
        .let {
            if (it.sender.userId == userId) it.sender.userName else it.receiver.userName
        }

    private fun getOpeningBalance(
        userId: UUID,
        status: Transaction.Status?,
        type: UserTransactionType?,
        earliestTransactionDate: LocalDateTime,
        startDate: LocalDateTime
    ): Double {
        if(earliestTransactionDate == startDate) return 0.0

        val transactions = transactionRepository.findFilteredTransactions(
            currentUserId = userId,
            status = status,
            transactionType = type?.name,
            startDate = earliestTransactionDate,
            endDate = startDate,
            pageable = Pageable.unpaged()
        )
        val transactionsCount = transactions.totalElements
        val openingBalance = transactions.sumOf { if (userId == it.sender.userId) it.amount.unaryMinus() else it.amount }.toDouble()
        return openingBalance
    }

    private fun getClosingBalance(
        openingBalance: Double,
        transactions: Page<Transaction>,
        userId: UUID
    ): Double {
        return openingBalance + transactions.sumOf { if (userId == it.sender.userId) it.amount.unaryMinus() else it.amount }
            .toDouble()
    }

    private fun mapTransactionForTemplate(currentUserId: UUID, transaction: Transaction): Map<String, Any> {
        return mapOf(
            "id" to "TX-" + transaction.id.toString().substring(0, 6).uppercase(),
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
            transaction.type == Transaction.Type.P2P && currentUserId == transaction.receiver.userId -> "Received from"
            transaction.type == Transaction.Type.P2P -> "Sent to"
            transaction.type == Transaction.Type.ONLINE_PURCHASE -> "Purchase from"
            else -> ""
        }
    }

    private fun getCounterParty(transaction: Transaction, currentUserId: UUID): String {
        return when {
            transaction.type == Transaction.Type.P2P && currentUserId == transaction.receiver.userId -> transaction.sender.userName
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

    fun getAppIconSvg(): String {
        return try {
            val resource = ResourceUtils.getFile("classpath:static/mena_logo.svg")
            val svgContent = resource.readText(Charsets.UTF_8)

            svgContent
                .replace("""<\?xml.*?\?>""".toRegex(), "")
                .trim()
        } catch (_: Exception) {
            ""
        }
    }

    private fun setResponseHeaders(response: HttpServletResponse, from: LocalDateTime, to: LocalDateTime) {
        response.contentType = "application/pdf"
        response.setHeader(
            "Content-Disposition",
            "attachment; filename=\"statement_${from.formatHeaderDate()}_to_${to.formatHeaderDate()}.pdf\""
        )
    }

    private fun LocalDateTime.formatHeaderDate(): String = this.format(DateTimeFormatter.ofPattern("dd MMM yyyy"))
    private fun LocalDateTime.formatRowItemDate(): String = this.format(DateTimeFormatter.ofPattern("yyyy/MM/dd"))
    private fun LocalDateTime.formatRowItemTime(): String = this.format(DateTimeFormatter.ofPattern("HH:mm a"))
}