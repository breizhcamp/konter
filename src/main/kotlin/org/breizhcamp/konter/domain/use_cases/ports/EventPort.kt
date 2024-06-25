package org.breizhcamp.konter.domain.use_cases.ports

import org.breizhcamp.konter.domain.entities.Event
import org.breizhcamp.konter.domain.entities.Talk

interface EventPort {

    fun existsById(id: Int): Boolean
    fun getById(id: Int): Event
    fun save(event: Event)
    fun save(events: List<Event>)
    fun exportTalks(id: Int): List<Talk>

}