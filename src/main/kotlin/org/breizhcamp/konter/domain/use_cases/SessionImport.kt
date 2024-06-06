package org.breizhcamp.konter.domain.use_cases

import mu.KotlinLogging
import org.apache.commons.csv.CSVFormat
import org.breizhcamp.konter.application.requests.EventCreationReq
import org.breizhcamp.konter.domain.entities.Evaluation
import org.breizhcamp.konter.domain.entities.Event
import org.breizhcamp.konter.domain.entities.Session
import org.breizhcamp.konter.domain.entities.enums.SessionFormatEnum
import org.breizhcamp.konter.domain.entities.enums.SessionNiveauEnum
import org.breizhcamp.konter.domain.entities.enums.SessionStatusEnum
import org.breizhcamp.konter.domain.entities.enums.SessionThemeEnum
import org.breizhcamp.konter.domain.use_cases.ports.EventPort
import org.breizhcamp.konter.domain.use_cases.ports.KalonPort
import org.breizhcamp.konter.domain.use_cases.ports.SessionPort
import org.breizhcamp.konter.domain.use_cases.ports.SpeakerPort
import org.springframework.stereotype.Service
import java.io.InputStream
import java.lang.Integer.parseInt
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

private val logger = KotlinLogging.logger {}

@Service
class SessionImport (
    private val sessionPort: SessionPort,
    private val speakerPort: SpeakerPort,
    private val eventPort: EventPort,
    private val kalonPort: KalonPort,
) {

    fun importCsv(year: Int, file: InputStream) {
        if (!eventPort.existsByYear(year)) {
            logger.info { "No Event with year=$year found, importing from Kalon" }

            eventPort.save(kalonPort.getEvents())
        }
        if (!eventPort.existsByYear(year)) {
            logger.info { "No Event with year=$year found, creating one" }

            eventPort.create(EventCreationReq(year))
        }
        val event: Event = eventPort.getByYear(year)

        val sessions = CSVFormat.Builder.create().apply {
            setIgnoreSurroundingSpaces(true)
            setIgnoreEmptyLines(true)
        }.build().parse(file.reader()).drop(1).map {
            val speakers = it.get(12)
                .split(", ")
                .map { str -> UUID.fromString(str) }
                .map { id -> speakerPort.get(id) }
            Session(
                id = parseInt(it[0].trim()),
                title = it[1].trim(),
                description = it[2].trim(),
                owner = speakerPort.getByNameAndEmail(it[3].trim(), it[4].trim()),
                speakers = speakers,
                format = SessionFormatEnum.getFromString(it[6].trim()),
                theme = SessionThemeEnum.getFromString(it[7].trim()),
                niveau = SessionNiveauEnum.getFromString(it[8].trim()),
                status = SessionStatusEnum.getFromString(it[9].trim()),
                submitted = LocalDateTime.parse(it[10].trim(),
                    DateTimeFormatter.ofPattern("d MMM yyyy hh:mm a")
                        .withLocale(Locale.FRENCH)),
                ownerNotes = it[11].trim(),
                event = event,
                videoURL = null,
                rating = null,
                slot = null,
            )
        }

        logger.info { "Saving [${sessions.size}] Sessions" }

        sessions.forEach(sessionPort::import)
    }

    fun importEvaluationCsv(file: InputStream) {
        val evaluations = CSVFormat.Builder.create().apply {
            setIgnoreSurroundingSpaces(true)
            setIgnoreEmptyLines(true)
        }.build().parse(file.reader()).drop(1).map {
            val session = sessionPort.getById(parseInt(it[0]))
            Evaluation(
                session = session,
                rating = it[9].replace(",", ".").toBigDecimal()
            )
        }

        logger.info { "Saving [${evaluations.size}] Evaluations" }

        evaluations.forEach(sessionPort::saveEvaluation)
    }
}