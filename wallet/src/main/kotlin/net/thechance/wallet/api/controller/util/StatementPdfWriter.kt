package net.thechance.wallet.api.controller.util

import com.itextpdf.html2pdf.ConverterProperties
import com.itextpdf.html2pdf.HtmlConverter
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.font.FontProvider
import net.thechance.wallet.api.dto.transaction.StatementData
import net.thechance.wallet.entity.Transaction
import net.thechance.wallet.service.StatementService
import net.thechance.wallet.service.TransactionService
import net.thechance.wallet.service.helper.UserTransactionType
import org.springframework.core.io.ResourceLoader
import org.springframework.data.domain.Page
import org.springframework.stereotype.Component
import java.io.OutputStream
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

@Component
class StatementPdfWriter(
    private val resourceLoader: ResourceLoader,
    private val statementHtmlGenerator: StatementHtmlGenerator,
    private val statementService: StatementService,
    private val transactionService: TransactionService,
) {
    fun writePdfToStream(
        userId: UUID,
        types: List<UserTransactionType>?,
        startDate: LocalDate?,
        endDate: LocalDate?,
        outputStream: OutputStream
    ) : StatementMetadata {
        val statementData = getStatementData(userId, types, startDate, endDate)

        val writer = PdfWriter(outputStream)
        val pdf = PdfDocument(writer)
        val converterProperties = setupConverterProperties()

        val metadata = writePages(statementData, pdf, converterProperties)

        pdf.close()
        outputStream.flush()

        return metadata
    }

    private fun writePages(
        statementData: StatementData,
        pdf: PdfDocument,
        converterProperties: ConverterProperties
    ) : StatementMetadata{
        var pageNum = 0
        var totalPages: Int
        var totalInflows: BigDecimal = 0.toBigDecimal()
        var totalOutflows: BigDecimal = 0.toBigDecimal()

        do {
            val page = statementService.getTransactionsPage(
                statementData.userId,
                statementData.startDateTime,
                statementData.endDateTime,
                statementData.types,
                pageNum
            )

            val htmlContent = statementHtmlGenerator.generateForPage(statementData, page)
            HtmlConverter.convertToPdf(htmlContent, pdf, converterProperties)

            totalInflows += getPageInflows(page, statementData.userId)
            totalOutflows += getPageOutflows(page, statementData.userId)

            totalPages = page.totalPages
            pageNum++
        } while (pageNum < totalPages)

        return StatementMetadata(
            startDate = statementData.startDateTime.toLocalDate(),
            endDate = statementData.endDateTime.toLocalDate().minusDays(1),
            totalInflows = totalInflows,
            totalOutflows = totalOutflows
        )
    }

    fun getStatementData(
        userId: UUID,
        types: List<UserTransactionType>?,
        startDate: LocalDate?,
        endDate: LocalDate?,
    ) : StatementData {
        val startDateTime = getStartDateTime(startDate, userId)
        val endDateTime = getEndDateTime(endDate)

        val openingBalance = statementService.getOpeningBalance(userId, startDate)
        val closingBalance = statementService.getClosingBalance(userId, endDate)

        return StatementData(
            userId = userId,
            username = statementService.getUserName(userId),
            startDateTime = startDateTime,
            endDateTime = endDateTime,
            openingBalance = openingBalance,
            closingBalance = closingBalance,
            types = types
        )
    }

    private fun getStartDateTime(startDate: LocalDate?, userId: UUID): LocalDateTime {
        return startDate?.atStartOfDay() ?: transactionService.getUserFirstTransactionDate(userId)
            ?: LocalDateTime.now()
    }

    private fun getEndDateTime(endDate: LocalDate?): LocalDateTime {
        return endDate?.plusDays(1)?.atStartOfDay() ?: LocalDateTime.now()
    }

    private fun setupConverterProperties(): ConverterProperties {
        val converterProperties = ConverterProperties()
        val fontProvider = FontProvider()

        listOf(
            "MadimiOne-Regular.ttf",
            "Poppins-Regular.ttf",
            "Poppins-Medium.ttf",
            "Poppins-SemiBold.ttf"
        ).forEach { fontPath ->
            val resource = resourceLoader.getResource("classpath:fonts/$fontPath")
            if (resource.exists()) {
                resource.inputStream.use { inputStream ->
                    fontProvider.addFont(inputStream.readAllBytes())
                }
            }
        }

        converterProperties.fontProvider = fontProvider
        converterProperties.isImmediateFlush = true
        return converterProperties
    }

    private fun getPageInflows(transactions: Page<Transaction>, userId: UUID): BigDecimal {
        return transactions.sumOf { transaction ->
            when {
                transaction.receiver.userId == userId -> transaction.amount
                else -> 0.toBigDecimal()
            }
        }
    }

    private fun getPageOutflows(transactions: Page<Transaction>, userId: UUID): BigDecimal {
        return transactions.sumOf { transaction ->
            when {
                transaction.sender.userId == userId -> transaction.amount
                else -> 0.toBigDecimal()
            }
        }
    }
}