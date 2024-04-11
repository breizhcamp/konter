package org.breizhcamp.konter.application.rest

import jakarta.servlet.http.HttpServletResponse
import mu.KotlinLogging
import org.breizhcamp.konter.application.dto.SessionDTO
import org.breizhcamp.konter.domain.entities.Session
import org.breizhcamp.konter.domain.use_cases.SessionGenerateCards
import org.breizhcamp.konter.domain.use_cases.SessionImport
import org.breizhcamp.konter.domain.use_cases.SessionList
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

private val logger = KotlinLogging.logger {}

@RestController
@RequestMapping("/api/sessions")
class SessionController (
    private val sessionImport: SessionImport,
    private val sessionGenerateCards: SessionGenerateCards,
    private val sessionList: SessionList,
) {

    @GetMapping("/{year}")
    fun listSessions(@PathVariable year: Int): List<SessionDTO> {
        logger.info { "Listing Sessions from year $year" }

        return sessionList.list(year).map { it.toDto() }
    }

    @PostMapping("/{year}/import")
    fun importCsv(@PathVariable year: Int, file: MultipartFile) {
        logger.info { "Importing Sessions for year $year" }

        sessionImport.importCsv(year, file.inputStream)
    }

    @PostMapping("/evaluations/import")
    fun importEvaluationsCsv(file: MultipartFile) {
        logger.info { "Importing Evaluations" }

        sessionImport.importEvaluationCsv(file.inputStream)
    }

    @GetMapping("/{year}/export")
    fun exportCards(@PathVariable year: Int, output: HttpServletResponse) {
        logger.info { "Generating Sessions cards for year $year" }

        output.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"session_cards_$year.pdf\"")
        output.contentType = MediaType.APPLICATION_PDF_VALUE
        sessionGenerateCards.generatePdf(year, output.outputStream)
    }

}

fun Session.toDto() = SessionDTO(
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
    hall = hall?.toDto(),
    beginning = beginning,
    end = end,
    videoURL = videoURL,
    rating = rating
)