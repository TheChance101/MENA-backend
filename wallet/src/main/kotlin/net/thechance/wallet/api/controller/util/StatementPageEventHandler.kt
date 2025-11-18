package net.thechance.wallet.api.controller.util

import com.itextpdf.io.font.PdfEncodings
import com.itextpdf.kernel.colors.DeviceRgb
import com.itextpdf.kernel.font.PdfFont
import com.itextpdf.kernel.font.PdfFontFactory
import com.itextpdf.kernel.geom.Rectangle
import com.itextpdf.kernel.pdf.canvas.PdfCanvas
import com.itextpdf.kernel.pdf.event.AbstractPdfDocumentEvent
import com.itextpdf.kernel.pdf.event.AbstractPdfDocumentEventHandler
import com.itextpdf.kernel.pdf.event.PdfDocumentEvent
import com.itextpdf.layout.Document
import com.itextpdf.svg.converter.SvgConverter
import net.thechance.wallet.service.model.output.StatementData
import org.springframework.core.io.ResourceLoader
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class StatementPageEventHandler(
    private val resourceLoader: ResourceLoader,
    private val statementData: StatementData
) : AbstractPdfDocumentEventHandler() {
    private val poppinsRegular by lazy { getPoppinsRegularFont() }
    private val poppinsSemiBold by lazy { getPoppinsSemiBoldFont() }
    private val madimiRegular by lazy { getMadimiRegularFont() }

    override fun onAcceptedEvent(event: AbstractPdfDocumentEvent?) {
        val docEvent = event as PdfDocumentEvent
        val pdfDoc = docEvent.document
        val page = docEvent.page
        val pageNumber = pdfDoc.getPageNumber(page)

        val canvas = PdfCanvas(page)
        val document = Document(pdfDoc)

        val pageSize = page.pageSize

        addHeader(canvas, document, pageSize)

        addFooter(canvas, pageSize, pageNumber)
    }

    private fun addHeader(canvas: PdfCanvas, document: Document, pageSize: Rectangle) {
        addStatementPeriod(canvas, poppinsSemiBold, poppinsRegular, pageSize)
        addMenaLogo(document, pageSize, canvas)
        addMenaText(canvas, pageSize)
    }

    private fun addMenaText(
        canvas: PdfCanvas,
        pageSize: Rectangle
    ) {
        val menaText = "MENA"
        val menaWidth = madimiRegular.getWidth(menaText, 14f)
        canvas.beginText()
            .setFontAndSize(madimiRegular, 14f)
            .setColor(DeviceRgb(39, 55, 77), true)
            .moveText((pageSize.right - 32 - menaWidth).toDouble(), (pageSize.top - 80).toDouble())
            .showText(menaText)
            .endText()
    }

    private fun addMenaLogo(
        document: Document,
        pageSize: Rectangle,
        canvas: PdfCanvas
    ) {
        try {
            val svgResource = resourceLoader.getResource("classpath:static/mena_logo.svg")
            val svgStream = svgResource.inputStream

            val processor = SvgConverter.convertToXObject(svgStream, document.pdfDocument)

            val logoWidth = 40f
            val logoX = pageSize.right - 32 - logoWidth
            val logoY = pageSize.top - 48 - 20f

            canvas.addXObjectAt(processor, logoX, logoY)

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun addStatementPeriod(
        canvas: PdfCanvas,
        titleFont: PdfFont,
        bodyFont: PdfFont,
        pageSize: Rectangle
    ) {
        canvas.beginText()
            .setFontAndSize(titleFont, 14f)
            .setColor(DeviceRgb(14, 16, 23), true)
            .moveText(32.0, (pageSize.top - 44).toDouble())
            .showText("Statement Period")
            .endText()

        canvas.beginText()
            .setFontAndSize(bodyFont, 12f)
            .setColor(DeviceRgb(62, 66, 82), true)
            .moveText(32.0, (pageSize.top - 70).toDouble())
            .showText("${statementData.startDateTime.formatHeaderDate()} – ${statementData.endDateTime.formatHeaderDate()}")
            .endText()
    }

    private fun getPoppinsRegularFont(): PdfFont {
        return PdfFontFactory.createFont(
            resourceLoader.getResource("classpath:fonts/Poppins-Regular.ttf").inputStream.readAllBytes(),
            PdfEncodings.IDENTITY_H
        )
    }

    private fun getPoppinsSemiBoldFont(): PdfFont {
        return PdfFontFactory.createFont(
            resourceLoader.getResource("classpath:fonts/Poppins-SemiBold.ttf").inputStream.readAllBytes(),
            PdfEncodings.IDENTITY_H
        )
    }

    private fun getMadimiRegularFont(): PdfFont {
        return PdfFontFactory.createFont(
            resourceLoader.getResource("classpath:fonts/MadimiOne-Regular.ttf").inputStream.readAllBytes(),
            PdfEncodings.IDENTITY_H
        )
    }

    private fun addFooter(
        canvas: PdfCanvas,
        pageSize: Rectangle,
        pageNumber: Int
    ) {
        val footerText = "Page $pageNumber"
        canvas.beginText()
            .setFontAndSize(poppinsRegular, 12f)
            .setColor(DeviceRgb(62, 66, 82), true)
            .moveText((pageSize.width / 2 - 40).toDouble(), 40.0)
            .showText(footerText)
            .endText()
    }

    private fun LocalDateTime.formatHeaderDate(): String =
        this.format(DateTimeFormatter.ofPattern("MMM dd yyyy"))
}