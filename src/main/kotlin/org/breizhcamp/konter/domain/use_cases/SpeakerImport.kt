package org.breizhcamp.konter.domain.use_cases

import mu.KotlinLogging
import org.apache.commons.csv.CSVFormat
import org.breizhcamp.konter.domain.entities.Speaker
import org.breizhcamp.konter.domain.use_cases.ports.SpeakerPort
import org.springframework.stereotype.Service
import java.io.InputStream
import java.util.*

private val logger = KotlinLogging.logger {}

@Service
class SpeakerImport (
    val speakerPort: SpeakerPort,
) {
    fun importCsv(file: InputStream) {
        val speakers = CSVFormat.Builder.create().apply {
            setIgnoreSurroundingSpaces(true)
            setIgnoreEmptyLines(true)
        }.build().parse(file.reader()).drop(1).map {
            Speaker(
                id = UUID.fromString(it[0].trim()),
                lastname = it[2].trim(),
                firstname = it[1].trim(),
                email = it[3].trim(),
                tagLine = it[4].trim(),
                bio = it[5].trim(),
                profilePicture = it[6].trim(),
            )
        }

        logger.info { "Saving [${speakers.size}] Speakers" }

        speakers.forEach(speakerPort::save)
    }
}