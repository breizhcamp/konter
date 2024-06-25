package org.breizhcamp.konter.infrastructure.db

import org.breizhcamp.konter.domain.entities.Event
import org.breizhcamp.konter.domain.entities.Talk
import org.breizhcamp.konter.domain.entities.exceptions.HallNotFoundException
import org.breizhcamp.konter.domain.use_cases.ports.EventPort
import org.breizhcamp.konter.infrastructure.db.mappers.toDB
import org.breizhcamp.konter.infrastructure.db.mappers.toEvent
import org.breizhcamp.konter.infrastructure.db.mappers.toHall
import org.breizhcamp.konter.infrastructure.db.mappers.toSpeaker
import org.breizhcamp.konter.infrastructure.db.model.EventDB
import org.breizhcamp.konter.infrastructure.db.model.HallDB
import org.breizhcamp.konter.infrastructure.db.model.SlotDB
import org.breizhcamp.konter.infrastructure.db.repos.EventRepo
import org.breizhcamp.konter.infrastructure.db.repos.HallRepo
import org.springframework.stereotype.Component
import java.time.LocalDate
import java.time.temporal.ChronoUnit

@Component
class EventAdapter (
    private val eventRepo: EventRepo,
    private val hallRepo: HallRepo
): EventPort {
    override fun existsById(id: Int): Boolean =
        eventRepo.existsById(id)

    @Throws
    override fun getById(id: Int): Event =
        eventRepo.findById(id).get().toEvent()

    override fun save(event: Event) {
        val oldEvent = eventRepo.findById(event.id)
        var eventToSave = event.toDB()
        if (oldEvent.isPresent) {
            val halls = oldEvent.get().halls
            eventToSave = eventToSave.copy(halls = halls)
        }
        eventRepo.save(eventToSave)
    }

    override fun save(events: List<Event>) = events.forEach { this.save(it) }
    override fun exportTalks(id: Int): List<Talk> {
        val event = eventRepo.findById(id).get()
        val halls = hallRepo.getAllByAvailableEventId(id)
        val slots = eventRepo.listSlotsHeld(id)

        val manualSessionSlots = slots.filter { it.manualSession != null && it.manualSession.speakers.isNotEmpty() }
        val sessionSlots = slots.filter { it.session != null && it.manualSession == null }

        return manualSessionSlots
            .asSequence()
            .map { it.toManualTalk(event, halls) }
            .plus(
                sessionSlots
                    .asSequence()
                    .map { it.toImportTalk(event, halls) }
            )
            .toList()
    }

}

fun SlotDB.toManualTalk(event: EventDB, availableHalls: List<HallDB>): Talk {
    val session = requireNotNull(manualSession)
    { "Slot toManualTalk called on slot with a null manualSession : Slot:$id" }
    val halls = availableHalls.filter { it in halls }

    if (halls.isEmpty()) {
        throw HallNotFoundException("No Hall in Slot:${id} corresponds to an hall in Event:${event.id}")
    }
    val date = computeDate(event)

    return Talk(
        id = session.id,
        name = session.title,
        eventStart = start.atDate(date),
        eventEnd = start.plus(duration).atDate(date),
        eventType = session.theme,
        format = session.format,
        hall = halls.first().toHall(),
        speakers = session.speakers.map { it.toSpeaker() },
        videoUrl = null,
        filesUrl = null,
        slidesUrl = null,
        description = session.description
    )
}

fun SlotDB.toImportTalk(event: EventDB, availableHalls: List<HallDB>): Talk {
    val session = requireNotNull(session)
    { "Slot toTalk called on slot with a null session : Slot:$id" }
    val halls = availableHalls.filter { it in halls }

    if (halls.isEmpty()) {
        throw HallNotFoundException("No Hall in Slot:${id} corresponds to an hall in Event:${event.id}")
    }
    val date = computeDate(event)

    return Talk(
        id = session.id,
        name = session.title,
        eventStart = start.atDate(date),
        eventEnd = start.plus(duration).atDate(date),
        eventType = session.theme,
        format = session.format,
        hall = halls.first().toHall(),
        speakers = session.speakers.map { it.toSpeaker() },
        videoUrl = null,
        filesUrl = null,
        slidesUrl = null,
        description = session.description
    )
}

fun SlotDB.computeDate(event: EventDB): LocalDate =
    requireNotNull(event.begin)
    { "Beginning of event should have been set before exporting talks" }
        .plus((day - 1).toLong(), ChronoUnit.DAYS)
