package org.breizhcamp.konter.domain.use_cases

import org.breizhcamp.konter.domain.entities.Event
import org.breizhcamp.konter.domain.use_cases.ports.EventPort
import org.springframework.stereotype.Service

@Service
class EventGet (
    private val eventPort: EventPort
) {
    fun getById(id: Int): Event = eventPort.getById(id)
}