package net.thechance.wallet.api.controller.util

import com.itextpdf.html2pdf.ConverterProperties
import com.itextpdf.html2pdf.HtmlConverter
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.kernel.pdf.event.PdfDocumentEvent
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.AreaBreak
import com.itextpdf.layout.element.IBlockElement
import com.itextpdf.layout.font.FontProvider
import com.itextpdf.layout.properties.AreaBreakType
import net.thechance.wallet.service.StatementService
import net.thechance.wallet.service.model.input.UserTransactionType
import net.thechance.wallet.service.model.output.StatementData
import org.springframework.core.io.ResourceLoader
import org.springframework.stereotype.Component
import java.io.OutputStream
import java.math.BigDecimal
import java.time.LocalDate
import java.util.*

@Component
class StatementPdfWriter(
    private val resourceLoader: ResourceLoader,
    private val statementHtmlGenerator: StatementHtmlGenerator,
    private val statementService: StatementService,
) {
    fun writePdfToStream(
        userId: UUID,
        types: List<UserTransactionType>?,
        startDate: LocalDate?,
        endDate: LocalDate?,
        outputStream: OutputStream
    ): StatementMetadata {
        val statementData = statementService.getStatementData(userId, types, startDate, endDate)

        val writer = PdfWriter(outputStream)
        val pdf = PdfDocument(writer)
        val document = Document(pdf)
        pdf.addEventHandler(PdfDocumentEvent.END_PAGE, StatementPageEventHandler(resourceLoader, statementData))
        document.setMargins(100f, 32f, 60f, 32f)
        val converterProperties = setupConverterProperties()

        val metadata = writePages(statementData, document, converterProperties)

        pdf.close()
        outputStream.flush()

        return metadata
    }

    private fun writePages(
        statementData: StatementData,
        pdf: Document,
        converterProperties: ConverterProperties
    ): StatementMetadata {
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
            val elements = HtmlConverter.convertToElements(htmlContent, converterProperties)
            elements.forEach { element ->
                pdf.add(element as IBlockElement)
            }

            totalInflows += statementService.getPageInflows(page, statementData.userId)
            totalOutflows += statementService.getPageOutflows(page, statementData.userId)

            totalPages = page.totalPages
            pageNum++

            if (pageNum < totalPages) {
                pdf.add(AreaBreak(AreaBreakType.NEXT_PAGE))
            }
        } while (pageNum < totalPages)

        return StatementMetadata(
            startDate = statementData.startDateTime.toLocalDate(),
            endDate = statementData.endDateTime.toLocalDate(),
            totalInflows = totalInflows,
            totalOutflows = totalOutflows
        )
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
}