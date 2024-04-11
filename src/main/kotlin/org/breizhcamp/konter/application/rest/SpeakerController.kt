package org.breizhcamp.konter.application.rest

import mu.KotlinLogging
import org.breizhcamp.konter.application.dto.SpeakerDTO
import org.breizhcamp.konter.domain.entities.Speaker
import org.breizhcamp.konter.domain.use_cases.SpeakerImport
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

private val logger = KotlinLogging.logger {}

@RestController
@RequestMapping("/api/speakers")
class SpeakerController (
    private val speakerImport: SpeakerImport,
) {

    @PostMapping("/import")
    fun importCsv(file: MultipartFile) {
        logger.info { "Importing Speakers" }

        speakerImport.importCsv(file.inputStream)
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