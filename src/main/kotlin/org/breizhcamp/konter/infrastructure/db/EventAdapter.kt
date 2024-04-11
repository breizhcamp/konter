package org.breizhcamp.konter.infrastructure.db

import org.breizhcamp.konter.application.dto.EventCreationReq
import org.breizhcamp.konter.domain.entities.Event
import org.breizhcamp.konter.domain.use_cases.ports.EventPort
import org.breizhcamp.konter.infrastructure.db.mappers.toDB
import org.breizhcamp.konter.infrastructure.db.mappers.toEvent
import org.breizhcamp.konter.infrastructure.db.repos.EventRepo
import org.springframework.stereotype.Component

@Component
class EventAdapter (
    private val eventRepo: EventRepo
): EventPort {
    override fun existsByYear(year: Int): Boolean =
        eventRepo.existsByYear(year)

    override fun getById(id: Int): Event =
        eventRepo.findById(id).get().toEvent()

    override fun getByYear(year: Int): Event =
        eventRepo.findByYear(year).toEvent()

    override fun save(event: Event) {
        eventRepo.save(event.toDB())
    }

    override fun save(events: List<Event>) = events.forEach { this.save(it) }

    override fun create(request: EventCreationReq): Int =
        eventRepo.createEvent(request.year)
}