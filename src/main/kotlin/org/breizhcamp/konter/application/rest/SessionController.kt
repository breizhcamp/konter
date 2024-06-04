package org.breizhcamp.konter.application.rest

import jakarta.servlet.http.HttpServletResponse
import mu.KotlinLogging
import org.breizhcamp.konter.application.dto.SessionDTO
import org.breizhcamp.konter.domain.entities.Session
import org.breizhcamp.konter.domain.entities.SessionFilter
import org.breizhcamp.konter.domain.use_cases.*
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.util.*

private val logger = KotlinLogging.logger {}

@RestController
@RequestMapping("/api/sessions")
class SessionController (
    private val sessionImport: SessionImport,
    private val sessionGenerateCards: SessionGenerateCards,
    private val eventGet: EventGet,
    private val sessionList: SessionList,
    private val slotSetSession: SlotSetSession,
) {

    @GetMapping("/{eventId}")
    fun listSessions(@PathVariable eventId: Int): List<SessionDTO> {
        logger.info { "Listing Sessions from eventId $eventId" }

        return sessionList.list(eventId).map { it.toDto() }
    }

    @PostMapping("/{eventId}/filter")
    fun filterSessions(@PathVariable eventId: Int, @RequestBody sessionFilter: SessionFilter): List<SessionDTO> {
        logger.info { "Filtering Sessions from Event:$eventId" }

        return sessionList.filter(eventId, sessionFilter).map { it.toDto() }
    }

    @PostMapping("/{eventId}/import")
    fun importCsv(@PathVariable eventId: Int, file: MultipartFile) {
        logger.info { "Importing Sessions for Event:$eventId" }

        sessionImport.importCsv(eventId, file.inputStream)
    }

    @PostMapping("/evaluations/import")
    fun importEvaluationsCsv(file: MultipartFile) {
        logger.info { "Importing Evaluations" }

        sessionImport.importEvaluationCsv(file.inputStream)
    }

    @GetMapping("/{eventId}/export")
    fun exportCards(@PathVariable eventId: Int, output: HttpServletResponse) {
        logger.info { "Generating Sessions cards for Event:$eventId" }
        val name = StringBuilder()
            .append("attachment; filename=")
            .append("\"session_cards_")
            .append(eventGet.getById(eventId).name)
            .append(".pdf\"")
            .toString()

        output.setHeader(HttpHeaders.CONTENT_DISPOSITION, name)
        output.contentType = MediaType.APPLICATION_PDF_VALUE
        sessionGenerateCards.generatePdf(eventId, output.outputStream)
    }

    @PostMapping("/{id}/slot/id/{slotId}")
    fun setSession(@PathVariable id: Int, @PathVariable slotId: UUID): SessionDTO {
        logger.info { "Setting Session:$id to Slot:$slotId" }

        return slotSetSession.setById(id, slotId).toDto()
    }

    @PostMapping("/{id}/slot/barcode/{barcode}")
    fun setSession(@PathVariable id: Int, @PathVariable barcode: String): SessionDTO {
        logger.info { "Setting Session:$id to Slot with barcode $barcode" }

        return slotSetSession.setByBarcode(id, barcode).toDto()
    }

}

fun Session.toDto(): SessionDTO = SessionDTO(
    id = id,
    title = title,
    description = description,
    owner = owner.toDto(),
    speakers = speakers.map { it.toDto() }.toList(),
    format = format,
    theme = theme,
    niveau = niveau,
    status = status,
    submitted = submitted,
    ownerNotes = ownerNotes,
    beginning = beginning,
    end = end,
    videoURL = videoURL,
    rating = rating,
    slot = slot?.toDto()
)