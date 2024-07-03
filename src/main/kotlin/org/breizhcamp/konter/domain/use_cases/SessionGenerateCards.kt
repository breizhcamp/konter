package org.breizhcamp.konter.domain.use_cases

import com.itextpdf.barcodes.BarcodeEAN
import com.itextpdf.io.font.constants.StandardFonts
import com.itextpdf.kernel.colors.Color
import com.itextpdf.kernel.colors.ColorConstants
import com.itextpdf.kernel.font.PdfFont
import com.itextpdf.kernel.font.PdfFontFactory
import com.itextpdf.kernel.geom.PageSize
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.Cell
import com.itextpdf.layout.element.Image
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.layout.element.Table
import com.itextpdf.layout.properties.HorizontalAlignment
import com.itextpdf.layout.properties.TextAlignment
import com.itextpdf.layout.properties.UnitValue
import com.itextpdf.layout.properties.VerticalAlignment
import mu.KotlinLogging
import org.apache.commons.lang3.StringUtils.substring
import org.breizhcamp.konter.domain.entities.enums.SessionThemeEnum
import org.breizhcamp.konter.domain.entities.enums.getLabel
import org.breizhcamp.konter.domain.use_cases.ports.SessionPort
import org.springframework.stereotype.Service
import java.io.OutputStream
import java.util.*

private val logger = KotlinLogging.logger {}

@Service
class SessionGenerateCards (
    private val sessionPort: SessionPort
) {

    // Fonts used in the document
    private val font: PdfFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD)
    private val fontColor: Color = ColorConstants.DARK_GRAY
    private val fontSize = 8f
    private val fontSize6 = 6f
    private val fontSize10 = 10f
    private val fontLight: PdfFont = PdfFontFactory.createFont(StandardFonts.HELVETICA)
    private val fontSizeLight = 5f

    fun generatePdf(eventId: Int, out: OutputStream) {

        // Define document size and margins
        val writer = PdfWriter(out)
        val pdfDoc = PdfDocument(writer).apply {
            defaultPageSize = PageSize.A4
        }
        val document = Document(pdfDoc).apply {
            setFont(font)
            setFontColor(fontColor)
            setMargins(10f, 10f, 10f, 10f)
        }

        // The document only has a full width table with three columns
        val table = Table(3).apply {
            width = UnitValue.createPercentValue(100f)
            setPadding(0f)
        }

        // Map from a SessionTheme to a color
        val bgTracksColor: MutableMap<SessionThemeEnum, Color> = EnumMap(SessionThemeEnum::class.java)

        // Create a card for each session in the given event
        val sessions = sessionPort.getAllByEventId(eventId, true)

        sessions.forEach {

            // A card is a full width table with two columns
            val innerTable = Table(2).apply {
                isKeepTogether = true
                width = UnitValue.createPercentValue(100f)
                setMargin(0f)
            }

            // One cell contains the format
            val formatPh = baseParagraph().apply {
                setPaddingBottom(0f)
                setVerticalAlignment(VerticalAlignment.BOTTOM)
                add(it.format.getLabel())
            }
            val format = Cell().apply {
                width = UnitValue.createPercentValue(50f)
                setPaddingTop(0f)
                setPaddingBottom(0f)
                add(formatPh)
            }
            innerTable.addCell(format)

            // One cell contains the theme and has a corresponding background color
            val themePh = baseParagraph().apply {
                setTextAlignment(TextAlignment.RIGHT)
                setVerticalAlignment(VerticalAlignment.BOTTOM)
                add(substring(it.theme.getLabel(), 0, 20))
            }
            val theme = Cell().apply {
                setHorizontalAlignment(HorizontalAlignment.RIGHT)
                setBackgroundColor(bgTracksColor.computeIfAbsent(it.theme) { getColor(bgTracksColor.size + 1) })
                setPaddingTop(0f)
                setPaddingBottom(0f)
                add(themePh)
            }
            innerTable.addCell(theme)

            // The second row has a full width cell
            val centralCell = Cell(1, 2).apply {
                isKeepTogether = true
                height = UnitValue.createPointValue(95.0f)
                setHorizontalAlignment(HorizontalAlignment.LEFT)
                setPaddingBottom(0f)
            }

            // Get the first speaker's short name
            // Example : Claire LUCAS -> C. LUCAS
            var speaker: String? = it.speakers.getOrNull(0)?.let { speaker ->
                var result = ""
                if (speaker.firstname.isNotEmpty()) {
                    result += speaker.firstname[0].toString() + ". "
                }
                result + speaker.lastname
            }

            // If they are co-speakers, add a "(+n)" at the end, n being the number of co-speakers
            if (it.speakers.size > 1) {
                speaker += " (+" + (it.speakers.size - 1) + ")"
            }

            // Add the computed speaker's name to the central cell
            val spk = baseParagraph().apply {
                setPadding(0f)
                setMarginTop(3f)
                setMarginBottom(1f)
                setTextAlignment(TextAlignment.CENTER)
                add(speaker)
            }

            centralCell.add(spk)

            // Add session's title to the central cell
            val ttl = baseParagraph().apply {
                isKeepTogether = true
                setFontSize(fontSize10)
                setFixedLeading(10f)
                setTextAlignment(TextAlignment.CENTER)
                setMarginBottom(0f)
                add(it.title)
            }
            centralCell.add(ttl)

            // Add the description to the central cell (if it exceeds 400 chars, it gets truncated to 400)
            val sizeMaxDesc = 400
            val desc: String = it.description
            val description = baseParagraph().apply {
                setFontSize(fontSize6)
                setTextAlignment(TextAlignment.JUSTIFIED)
                setMarginTop(3f)
                setMarginBottom(0f)
            }

            // Truncate the description if it exceeds 400 characters
            description.add(if (desc.length > sizeMaxDesc) {
                desc.substring(0, sizeMaxDesc).replace("\n\n".toRegex(), "\n")
            } else {
                desc.replace("\n\n".toRegex(), "\n")
            })

            centralCell.add(description)
            innerTable.addCell(centralCell)

            // Create the barcode with the session's id
            val code = "9" + it.id.toString().padStart(6, '0')
            val barcode = BarcodeEAN(pdfDoc).apply {
                this.code = code + BarcodeEAN.calculateEANParity(code)
                codeType = BarcodeEAN.EAN8
                barHeight = 13f
                font = null
            }
            sessionPort.addBarcode(it.id, barcode.code)

            // Add the barcode at the third row, first cell
            val barcodePh = baseParagraph().apply {
                setMargin(0f)
                setPadding(0f)
                setFont(fontLight)
                setFontSize(fontSizeLight)
                add(Image(barcode.createFormXObject(pdfDoc)))
                add("     " + it.id)
            }

            val barCell = Cell().apply {
                setHorizontalAlignment(HorizontalAlignment.LEFT)
                setVerticalAlignment(VerticalAlignment.BOTTOM)
                setPadding(1f)
                setPaddingBottom(0f)
                setPaddingLeft(8f)
                height = UnitValue.createPointValue(15f)
                add(barcodePh)
            }

            innerTable.addCell(barCell)

            // Add the session's evaluation result to the last cell
            val notePh = baseParagraph().apply {
                setMarginBottom(0f)
                setMarginTop(0f)
                setVerticalAlignment(VerticalAlignment.BOTTOM)
                add("note : " + (it.rating?.toString() ?: "N/A"))
            }
            val note = Cell().apply {
                setHorizontalAlignment(HorizontalAlignment.LEFT)
                setVerticalAlignment(VerticalAlignment.BOTTOM)
                setMaxHeight(barCell.height)
                setPaddingBottom(0f)
                setPaddingTop(0f)
                add(notePh)
            }

            innerTable.addCell(note)
            table.addCell(innerTable)
        }

        // Fill the last row of the table with empty cells to get to a multiple of three
        for (i in 0 until 3 - sessions.size % 3) {
            table.addCell("")
        }

        // Add the table to the document and close the output stream
        document.add(table)
        document.close()

        logger.info { "Generated Session cards for Event:$eventId" }
    }

    private fun getColor(idx: Int): Color {
        return when (idx) {
            1 -> ColorConstants.MAGENTA
            2 -> ColorConstants.PINK
            3 -> ColorConstants.YELLOW
            4 -> ColorConstants.GREEN
            5 -> ColorConstants.ORANGE
            6 -> ColorConstants.GRAY
            7 -> ColorConstants.RED
            8 -> ColorConstants.CYAN
            9 -> ColorConstants.LIGHT_GRAY
            else -> ColorConstants.WHITE
        }
    }

    private fun baseParagraph() = Paragraph().apply {
        setFont(font)
        setFontSize(fontSize)
        setFontColor(fontColor)
    }
}