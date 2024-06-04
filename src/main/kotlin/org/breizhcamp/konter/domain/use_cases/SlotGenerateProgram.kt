package org.breizhcamp.konter.domain.use_cases

import com.itextpdf.barcodes.BarcodeEAN
import com.itextpdf.io.font.constants.StandardFonts
import com.itextpdf.kernel.font.PdfFontFactory
import com.itextpdf.kernel.geom.PageSize
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.Cell
import com.itextpdf.layout.element.Image
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.layout.element.Table
import com.itextpdf.layout.layout.LayoutArea
import com.itextpdf.layout.layout.LayoutContext
import com.itextpdf.layout.properties.HorizontalAlignment
import com.itextpdf.layout.properties.TextAlignment
import com.itextpdf.layout.properties.UnitValue
import com.itextpdf.layout.properties.VerticalAlignment
import org.breizhcamp.konter.domain.use_cases.ports.HallPort
import org.breizhcamp.konter.domain.use_cases.ports.SlotPort
import org.springframework.stereotype.Service
import java.io.OutputStream
import java.time.Duration
import java.time.LocalTime

@Service
class SlotGenerateProgram (
    private val slotPort: SlotPort,
    private val hallPort: HallPort
) {

    fun generateEmptyProgramPdf(eventId: Int, out: OutputStream) {
        val font = PdfFontFactory.createFont(StandardFonts.HELVETICA)
        val bold = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD)

        val titleSize = 40f
        val textSize = 18f

        val segmentMinutes = 15L

        // Define document size, orientation and margins
        val writer = PdfWriter(out)
        val pdfDoc = PdfDocument(writer)
        pdfDoc.defaultPageSize = PageSize.A3
        val document = Document(pdfDoc)
        document.setFont(font)
        document.setFontSize(textSize)
        document.setMargins(10f, 10f, 10f, 10f)


        val program = slotPort.getProgram(eventId).toSortedMap()
        val halls = hallPort.list(eventId)

        program.onEachIndexed { pageNumber, (day, tracks) ->
            // Set title
            val title = Paragraph().setTextAlignment(TextAlignment.CENTER).setFont(bold).setFontSize(titleSize).add("Programme jour $day")
            document.add(title)

            val preRenderer = title.createRendererSubTree().setParent(document.renderer)
            val preRender = preRenderer.layout(LayoutContext(LayoutArea(pageNumber, PageSize.A3)))
            val remainingHeight = PageSize.A3.height - preRender.occupiedArea.bBox.height

            // Add a table for each day, with one column for each hall
            val table = Table(halls.size).apply {
                width = UnitValue.createPercentValue(100f)
                isKeepTogether = true
                height = UnitValue.createPointValue(remainingHeight)
                setMarginTop(10f)
            }

            // Table header
            halls.forEach {
                val hallName = Paragraph().apply {
                    setFont(bold)
                    setTextAlignment(TextAlignment.CENTER)
                    add(it.name)
                }

                val hallCell = Cell().apply {
                    setHorizontalAlignment(HorizontalAlignment.CENTER)
                    setVerticalAlignment(VerticalAlignment.MIDDLE)
                    height = UnitValue.createPointValue(25f)
                    add(hallName)
                }

                table.addCell(hallCell)
            }

            val allSlots = tracks.flatMap { (_, slots) -> slots }

            if (allSlots.isNotEmpty()) {
                var minTime = allSlots.minOf { it.start }
                minTime -= Duration.ofMinutes(minTime.minute.mod(segmentMinutes))

                var maxTime = allSlots.maxOf { it.start.plus(it.duration) }
                if (maxTime.minute.mod(15) != 0) {
                    maxTime += Duration.ofMinutes(15 - maxTime.minute.mod(segmentMinutes))
                }

                val segments = minTime.until(maxTime, java.time.temporal.ChronoUnit.MINUTES).div(segmentMinutes)

                for (i in 0..<segments) {
                    val time = minTime.plus(Duration.ofMinutes(15 * i))

                    var occupiedColumns = 0

                    halls.forEachIndexed { index, hall ->
                        if (tracks.containsKey(hall)) {
                            val slot = tracks[hall]!!.find { it.start == time || (it.start.isAfter(time) && it.start.isBefore(time.plus(Duration.ofMinutes(segmentMinutes)))) }
                            if (slot == null) {
                                val slotsOverTime = tracks[hall]!!.filter { it.start.isBefore(time) && it.start.plus(it.duration).isAfter(time) }
                                if (slotsOverTime.isNotEmpty()) {
                                    occupiedColumns += slotsOverTime.first().span
                                }
                            } else {
                                var rowspan = slot.duration.toMinutes()
                                if (rowspan.mod(segmentMinutes) != 0L) {
                                    rowspan += (segmentMinutes - rowspan.mod(segmentMinutes))
                                }

                                if (occupiedColumns < index) {
                                    for (j in 0..<index-occupiedColumns) {
                                        table.addCell(Cell())
                                        occupiedColumns++
                                    }
                                }

                                val cell = Cell(rowspan.div(segmentMinutes).toInt(), slot.span)
                                occupiedColumns += slot.span

                                val cellTitle = Paragraph().apply {
                                    setTextAlignment(TextAlignment.CENTER)
                                    add(formatTime(slot.start))
                                    add(" - ")
                                    add(formatTime(slot.start.plus(slot.duration)))
                                }
                                val cellBarcode = BarcodeEAN(pdfDoc).apply {
                                    codeType = BarcodeEAN.EAN13
                                    code = slot.barcode
                                }
                                cell.apply {
                                    setHorizontalAlignment(HorizontalAlignment.CENTER)
                                    setVerticalAlignment(VerticalAlignment.MIDDLE)
                                    add(cellTitle)
                                    add(Image(cellBarcode.createFormXObject(pdfDoc)).setHorizontalAlignment(HorizontalAlignment.CENTER))
                                }
                                table.addCell(cell)
                            }
                        }
                        if (index == halls.size - 1) {
                            for (j in occupiedColumns..<halls.size) {
                                table.addCell(Cell())
                            }
                        }
                    }
                }
            }

            document.add(table)

        }

        document.close()
    }

    private fun formatTime(time: LocalTime): String {
        val hour = "${ time.hour }".padStart(2, '0')
        val minute = "${ time.minute }".padStart(2, '0')

        return "${hour}h$minute"
    }
}