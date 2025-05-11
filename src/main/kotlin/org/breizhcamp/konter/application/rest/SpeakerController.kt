package org.breizhcamp.konter.application.rest

import mu.KotlinLogging
import org.breizhcamp.konter.application.dto.SpeakerDTO
import org.breizhcamp.konter.application.dto.SpeakerExportDTO
import org.breizhcamp.konter.domain.entities.Speaker
import org.breizhcamp.konter.domain.use_cases.SpeakerImport
import org.breizhcamp.konter.domain.use_cases.SpeakerExport
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping

private val logger = KotlinLogging.logger {}

@RestController
@RequestMapping("/api/speakers")
class SpeakerController (
    private val speakerImport: SpeakerImport,
    private val speakerExport: SpeakerExport,
) {

    @PostMapping("/import")
    fun importCsv(file: MultipartFile) {
        logger.info { "Importing Speakers" }

        speakerImport.importCsv(file.inputStream)
    }

    @GetMapping("/export")
    fun exportSpeakers(): ResponseEntity<List<SpeakerExportDTO>> {
        logger.info { "Exporting Speakers" }
        val speakers = speakerExport.export().map { it.toExportDto() }
        return ResponseEntity.ok(speakers)
    }

}

fun Speaker.toDto() = SpeakerDTO(
    id = id,
    lastname = lastname,
    firstname = firstname,
    email = email,
    tagLine = tagLine,
    bio = bio,
    profilePicture = profilePicture
)

fun Speaker.toExportDto() = SpeakerExportDTO(
    id = id,
    lastname = lastname,
    firstname = firstname,
    imageProfilURL = if (profilePicture.isBlank()) null else profilePicture,
    bio = bio,
    github = null,
    googleplus = null,
    twitter = null,
    social = null
)
