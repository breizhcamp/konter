package org.breizhcamp.konter.domain.use_cases.ports

import org.breizhcamp.konter.application.dto.EventCreationReq
import org.breizhcamp.konter.domain.entities.Event

interface EventPort {

    fun existsByYear(year: Int): Boolean
    fun getById(id: Int): Event
    fun getByYear(year: Int): Event
    fun save(event: Event)
    fun save(events: List<Event>)
    fun create(request: EventCreationReq): Int

}