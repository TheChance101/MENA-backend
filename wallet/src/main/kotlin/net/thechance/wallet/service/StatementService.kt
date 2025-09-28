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
        startDate: LocalDate,
        endDate: LocalDate,
        type: UserTransactionType?,
        status: Transaction.Status?,
        response: HttpServletResponse
    ) {
        try {
            val transactions = transactionRepository.findFilteredTransactions(
                status = status,
                transactionType = type?.name,
                startDate = startDate.atStartOfDay(),
                endDate = endDate.atTime(23, 59, 59, 59),
                pageable = Pageable.unpaged(),
                currentUserId = userId
            )

            if (transactions.isEmpty) {
                response.status = HttpServletResponse.SC_NO_CONTENT
                response.writer.write("No transactions found for the specified filters")
                return
            }
            val username = getUsername(transactions, userId)

            val openingBalance = getOpeningBalance(userId, status, type, startDate)

            val closingBalance = getClosingBalance(openingBalance, transactions, userId)

            val templateData = mutableMapOf<String?, Any?>(
                "userName" to username,
                "fromFormatted" to startDate.formatHeaderDate(),
                "toFormatted" to endDate.formatHeaderDate(),
                "openingBalance" to String.format("%.2f", openingBalance),
                "closingBalance" to String.format("%.2f", closingBalance),
                "logoSvgInline" to getAppIconSvg(),
                "transactions" to transactions.map { mapTransactionForTemplate(userId, it) }
            )

            setResponseHeaders(response, startDate, endDate)

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
        startDate: LocalDate
    ): Double = transactionRepository.findFilteredTransactions(
        currentUserId = userId,
        status = status,
        transactionType = type?.name,
        startDate = null,
        endDate = startDate.atStartOfDay(),
        pageable = Pageable.unpaged()
    ).sumOf { if (userId == it.sender.userId) it.amount.unaryMinus() else it.amount }.toDouble()

    private fun getClosingBalance(
        openingBalance: Double,
        transactions: Page<Transaction>,
        userId: UUID
    ): Double =
        openingBalance + transactions.sumOf { if (userId == it.sender.userId) it.amount.unaryMinus() else it.amount }
            .toDouble()

    private fun mapTransactionForTemplate(currentUserId: UUID, transaction: Transaction): Map<String, Any> {
        return mapOf(
            "id" to "TX-" + transaction.id.toString().substring(0, 6).uppercase(),
            "date" to transaction.createdAt.formatRowItemDate(),
            "time" to transaction.createdAt.formatRowItemTime(),
            "typeHeader" to getTypeHeader(transaction, currentUserId),
            "counterParty" to getCounterParty(transaction, currentUserId),
            "amount" to getFormattedAmount(currentUserId, transaction),
            "amountValue" to transaction.amount
        )
    }

    private fun getTypeHeader(transaction: Transaction, currentUserId: UUID): String = when {
        transaction.type == Transaction.Type.P2P && currentUserId == transaction.receiver.userId -> "Received from"
        transaction.type == Transaction.Type.P2P -> "Sent to"
        transaction.type == Transaction.Type.ONLINE_PURCHASE -> "Purchase from"
        else -> ""
    }

    private fun getCounterParty(transaction: Transaction, currentUserId: UUID): String = when {
        transaction.type == Transaction.Type.P2P && currentUserId == transaction.receiver.userId -> transaction.sender.userName
        transaction.type == Transaction.Type.P2P -> transaction.receiver.userName
        transaction.type == Transaction.Type.ONLINE_PURCHASE -> transaction.receiver.dukanName ?: ""
        else -> ""
    }

    private fun getFormattedAmount(currentUserId: UUID, transaction: Transaction): String =
        if (currentUserId == transaction.sender.userId) {
            "-${String.format("%.2f", transaction.amount)}"
        } else {
            "+${String.format("%.2f", transaction.amount)}"
        }

    private fun getAmountValue(currentUserId: UUID, transaction: Transaction): String =
        if (currentUserId == transaction.sender.userId) {
            "-${String.format("%.2f", transaction.amount)}"
        } else {
            "+${String.format("%.2f", transaction.amount)}"
        }

    fun getAppIconSvg(): String {
        val resource = ResourceUtils.getFile("classpath:static/mena_logo.svg")
        val svgContent = resource.readText(Charsets.UTF_8)

        return svgContent
            .replace("""<\?xml.*?\?>""".toRegex(), "")
            .trim()
    }

    private fun setResponseHeaders(response: HttpServletResponse, from: LocalDate, to: LocalDate) {
        response.contentType = "application/pdf"
        response.setHeader(
            "Content-Disposition",
            "attachment; filename=\"statement_${from}_to_${to}.pdf\""
        )
    }

    private fun LocalDate.formatHeaderDate(): String = this.format(DateTimeFormatter.ofPattern("dd MMM yyyy"))
    private fun LocalDateTime.formatRowItemDate(): String = this.format(DateTimeFormatter.ofPattern("yyyy/MM/dd"))
    private fun LocalDateTime.formatRowItemTime(): String = this.format(DateTimeFormatter.ofPattern("HH:mm a"))
}