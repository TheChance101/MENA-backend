package net.thechance.wallet.api.controller.helper

import com.itextpdf.html2pdf.ConverterProperties
import com.itextpdf.html2pdf.HtmlConverter
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.font.FontProvider
import jakarta.servlet.ServletOutputStream
import net.thechance.wallet.api.dto.transaction.StatementData
import net.thechance.wallet.api.dto.transaction.UserTransactionType
import net.thechance.wallet.service.StatementService
import net.thechance.wallet.service.TransactionService
import net.thechance.wallet.service.helper.StatementHtmlGenerator
import org.springframework.core.io.ResourceLoader
import org.springframework.stereotype.Component
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

@Component
class StatementPdfGenerator(
    private val resourceLoader: ResourceLoader,
    private val statementHtmlGenerator: StatementHtmlGenerator,
    private val statementService: StatementService,
    private val transactionService: TransactionService,
) {
    fun generatePdf(
        userId: UUID,
        types: List<UserTransactionType>?,
        startDate: LocalDate?,
        endDate: LocalDate?,
        outputStream: ServletOutputStream
    ) {
        val statementData = getStatementData(
            userId = userId,
            types = types,
            startDate = startDate,
            endDate = endDate,
        )

        val writer = PdfWriter(outputStream)
        val pdf = PdfDocument(writer)
        val converterProperties = setupConverterProperties()

        val firstPage = statementService.getTransactionsPage(
            userId = userId,
            startDateTime = statementData.startDateTime,
            endDateTime = statementData.endDateTime,
            types = types,
            pageNum = 0
        )

        for (pageNum in 0 until firstPage.totalPages) {
            val page = if (pageNum == 0) firstPage else statementService.getTransactionsPage(
                userId = userId,
                startDateTime = statementData.startDateTime,
                endDateTime = statementData.endDateTime,
                types = types,
                pageNum = pageNum
            )

            val htmlContent = statementHtmlGenerator.generateForPage(
                statementData = statementData,
                transactionsPage = page
            )

            HtmlConverter.convertToPdf(htmlContent, pdf, converterProperties)
        }

        pdf.close()
        outputStream.flush()
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

    private companion object {
        const val PAGE_SIZE = 100
    }
}