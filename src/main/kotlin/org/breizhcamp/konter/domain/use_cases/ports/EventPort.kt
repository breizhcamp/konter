package org.breizhcamp.konter.domain.use_cases.ports

import org.breizhcamp.konter.domain.entities.Event

interface EventPort {

    fun existsById(id: Int): Boolean
    fun getById(id: Int): Event
    fun save(event: Event)
    fun save(events: List<Event>)

}