package org.breizhcamp.konter.domain.use_cases

import mu.KotlinLogging
import org.apache.commons.csv.CSVFormat
import org.breizhcamp.konter.domain.entities.Assignation
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
import org.breizhcamp.konter.domain.use_cases.ports.SlotPort
import org.breizhcamp.konter.domain.use_cases.ports.SpeakerPort
import org.springframework.stereotype.Service
import java.io.InputStream
import java.lang.Integer.parseInt
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.*

private val logger = KotlinLogging.logger {}

@Service
class SessionImport (
    private val sessionPort: SessionPort,
    private val speakerPort: SpeakerPort,
    private val eventPort: EventPort,
    private val kalonPort: KalonPort,
    private val slotPort: SlotPort,
) {

    fun importCsv(eventId: Int, file: InputStream) {
        if (!eventPort.existsById(eventId)) {
            logger.info { "No Event with id=$eventId found, importing from Kalon" }

            eventPort.save(kalonPort.getEvents())
        }
        if (!eventPort.existsById(eventId)) {
            logger.info { "No Event with id=$eventId found, exiting" }

            file.close()
            return
        }
        val event: Event = eventPort.getById(eventId)

        val sessions = CSVFormat.Builder.create().apply {
            setIgnoreSurroundingSpaces(true)
            setIgnoreEmptyLines(true)
        }.build().parse(file.reader()).drop(1).map {
            val speakers = it[13]
                .split(", ")
                .map { str -> UUID.fromString(str) }
                .map { id -> speakerPort.get(id) }
            Session(
                id = parseInt(it[0].trim()),
                title = it[1].trim(),
                description = it[2].trim(),
                owner = speakerPort.getByNameAndEmail(it[3].trim(), it[4].trim()),
                speakers = speakers,
                format = SessionFormatEnum.getFromString(it[6].trim().split(", ").first()), // TODO handle multiple formats
                theme = SessionThemeEnum.getFromString(it[7].trim()),
                niveau = SessionNiveauEnum.getFromString(it[8].trim()),
                status = SessionStatusEnum.getFromString(it[10].trim()),
                submitted = LocalDateTime.parse(it[11].trim(),
                    DateTimeFormatter.ofPattern("d MMM yyyy hh:mm a")
                        .withLocale(Locale.FRENCH)),
                ownerNotes = it[12].trim(),
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
                rating = it[10].replace(",", ".").toBigDecimal()
            )
        }

        logger.info { "Saving [${evaluations.size}] Evaluations" }

        evaluations.forEach(sessionPort::saveEvaluation)
    }

    fun importSchedule(csv: InputStream, eventId: Int) {
        val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy hh:mm a", Locale.FRENCH)
        val event: Event = eventPort.getById(eventId)

        val schedules = CSVFormat.Builder.create().apply {
            setIgnoreSurroundingSpaces(true)
            setIgnoreEmptyLines(true)
        }.build().parse(csv.reader()).drop(1).map {
            val sessionDate = LocalDateTime.parse(it[13].trim(), formatter)
            val time = sessionDate.toLocalTime()
            val day = ChronoUnit.DAYS.between(event.begin, sessionDate.toLocalDate()).toInt() + 1

            Assignation(
                sessionId = it[0].trim().toInt(),
                day = day,
                start = time,
                room = it[12].trim(),
            )
        }

        logger.info("Saving [${schedules.size}] schedules")
        slotPort.clearSchedule(eventId)
        slotPort.importSchedule(eventId, schedules)
    }
}
