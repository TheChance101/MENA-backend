package net.thechance.wallet.api.controller.helper

import com.itextpdf.html2pdf.ConverterProperties
import com.itextpdf.html2pdf.HtmlConverter
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.font.FontProvider
import jakarta.servlet.ServletOutputStream
import net.thechance.wallet.service.helper.StatementData
import net.thechance.wallet.service.helper.StatementHtmlGenerator
import org.springframework.core.io.ResourceLoader
import org.springframework.stereotype.Component

@Component
class PdfConverter(
    private val resourceLoader: ResourceLoader,
    private val statementHtmlGenerator: StatementHtmlGenerator
) {

    fun convertToPdfStream(statementData: StatementData, outputStream: ServletOutputStream) {
        val writer = PdfWriter(outputStream)
        val pdf = PdfDocument(writer)

        val converterProperties = setupConverterProperties()

        for (pageNum in 0 until statementData.totalPages) {
            val transactions = statementData.transactionProvider(pageNum)

            val htmlContent = statementHtmlGenerator.generateForPage(
                statementData = statementData,
                transactions = transactions,
                pageNum = pageNum
            )

            HtmlConverter.convertToPdf(htmlContent, pdf, converterProperties)
        }

        pdf.close()
        outputStream.flush()
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