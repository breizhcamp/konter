package org.breizhcamp.konter.domain.use_cases

import org.breizhcamp.konter.domain.entities.Session
import org.breizhcamp.konter.domain.entities.SessionFilter
import org.breizhcamp.konter.domain.use_cases.ports.SessionPort
import org.springframework.stereotype.Service

@Service
class SessionList(
    private val sessionPort: SessionPort
) {
    fun list(eventId: Int): List<Session> = sessionPort.getAllByEventId(eventId, false)
    fun filter(eventId: Int, filter: SessionFilter): List<Session> = sessionPort.filterByEventId(eventId, filter)
}