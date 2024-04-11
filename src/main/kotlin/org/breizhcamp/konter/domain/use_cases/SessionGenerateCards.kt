package org.breizhcamp.konter.domain.use_cases

import com.itextpdf.text.*
import com.itextpdf.text.pdf.*
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

    @Throws(DocumentException::class)
    fun generatePdf(year: Int, out: OutputStream) {

        // Define document size and margins
        val document = Document(PageSize.A4, 10f, 10f, 10f, 10f)
        val writer = PdfWriter.getInstance(document, out)

        // Fonts used in the document
        val font = Font(Font.FontFamily.HELVETICA, 8f, Font.BOLD, BaseColor.DARK_GRAY)
        val font6 = Font(Font.FontFamily.HELVETICA, 6f, Font.BOLD, BaseColor.DARK_GRAY)
        val font10 = Font(Font.FontFamily.HELVETICA, 10f, Font.BOLD, BaseColor.DARK_GRAY)
        val fontLight = Font(Font.FontFamily.HELVETICA, 5f, Font.NORMAL, BaseColor.DARK_GRAY)

        document.open()

        // The document only has a full width table with three columns
        val table = PdfPTable(3)
        table.widthPercentage = 100f

        // Map from a SessionTheme to a color
        val bgTracksColor: MutableMap<SessionThemeEnum, BaseColor> = EnumMap(SessionThemeEnum::class.java)

        // Create a card for each session in the given year
        val sessions = sessionPort.getAllByEventYear(year)
        sessions.forEach {

            // A card is a full width table with two columns
            val innerTable = PdfPTable(2)
            innerTable.widthPercentage = 100f

            // One cell contains the format
            val formatPh = Phrase(Chunk(it.format.getLabel(), font))
            val format = PdfPCell(formatPh)
            innerTable.addCell(format)

            // One cell contains the theme and has a corresponding background color
            val track = PdfPCell(Phrase(substring(it.theme.getLabel(), 0, 20), font))
            track.horizontalAlignment = Element.ALIGN_RIGHT
            track.backgroundColor = bgTracksColor.computeIfAbsent(it.theme) { getColor(bgTracksColor.size + 1) }
            innerTable.addCell(track)

            // The second row has a full width cell
            val centralCell = PdfPCell()
            centralCell.colspan = 2
            centralCell.horizontalAlignment = Element.ALIGN_JUSTIFIED
            centralCell.fixedHeight = 100.0f

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
            val spk = Paragraph(speaker, font)
            spk.alignment = Paragraph.ALIGN_CENTER
            centralCell.addElement(spk)

            // Add session's title to the central cell
            val ttl = Paragraph(10f, it.title, font10)
            ttl.alignment = Paragraph.ALIGN_CENTER
            ttl.spacingBefore = 3f
            ttl.keepTogether = true
            centralCell.addElement(ttl)

            // Add the description to the central cell (if it exceeds 400 chars, it gets truncated to 400)
            val description: Paragraph
            val sizeMaxDesc = 400
            val desc: String = it.description
            description = if (desc.length > sizeMaxDesc) {
                Paragraph(Paragraph(desc.substring(0, sizeMaxDesc).replace("\n\n".toRegex(), "\n"), font6))
            } else {
                Paragraph(Paragraph(desc.replace("\n\n".toRegex(), "\n"), font6))
            }
            description.alignment = Paragraph.ALIGN_JUSTIFIED
            description.spacingBefore = 3f
            centralCell.addElement(description)
            innerTable.addCell(centralCell)

            // Create the barcode with the session's id
            val barcode = BarcodeEAN()
            barcode.codeType = Barcode.EAN8
            val code = "9" + it.id.toString().padStart(6, '0')
            barcode.code = code + BarcodeEAN.calculateEANParity(code)
            barcode.barHeight = 15f
            barcode.font = null

            // Add the barcode at the third row, first cell
            val barcodePh = Phrase()
            val barCell = PdfPCell(barcodePh)
            barcodePh.add(Chunk(barcode.createImageWithBarcode(writer.directContent, null, null), 0f, 0f))
            barcodePh.add(Chunk("     " + it.id, fontLight))
            barCell.horizontalAlignment = Element.ALIGN_LEFT
            barCell.verticalAlignment = Element.ALIGN_BOTTOM
            barCell.setPadding(1f)
            barCell.paddingLeft = 8f
            barCell.fixedHeight = 15f
            innerTable.addCell(barCell)

            // Add the session's evaluation result to the last cell
            val note = PdfPCell(Phrase("note : " + (it.rating?.toString() ?: "N/A"), font))
            note.horizontalAlignment = Element.ALIGN_LEFT
            note.verticalAlignment = Element.ALIGN_BOTTOM
            innerTable.addCell(note)
            table.addCell(innerTable)
        }

        // Fill the last row of the table with empty cells to get to a multiple of three
        for (i in 0 until sessions.size % 3) {
            table.addCell("")
        }

        // Add the table to the document and close the output stream
        document.add(table)
        document.close()

        logger.info { "Generated Session cards for year $year" }
    }

    private fun getColor(idx: Int): BaseColor {
        return when (idx) {
            1 -> BaseColor.MAGENTA
            2 -> BaseColor.PINK
            3 -> BaseColor.YELLOW
            4 -> BaseColor.GREEN
            5 -> BaseColor.ORANGE
            6 -> BaseColor.GRAY
            7 -> BaseColor.RED
            8 -> BaseColor.CYAN
            9 -> BaseColor.LIGHT_GRAY
            else -> BaseColor.WHITE
        }
    }
}