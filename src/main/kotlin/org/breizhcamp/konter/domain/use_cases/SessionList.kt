package org.breizhcamp.konter.domain.use_cases

import org.breizhcamp.konter.domain.entities.Session
import org.breizhcamp.konter.domain.entities.SessionFilter
import org.breizhcamp.konter.domain.use_cases.ports.SessionPort
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

@Service
class SessionList(
    private val sessionPort: SessionPort
) {
    fun list(eventId: Int, page: Pageable): Page<Session> = sessionPort.getAllByEventId(eventId, false, page)
    fun filter(eventId: Int, filter: SessionFilter, page: Pageable): Page<Session> = sessionPort.filterByEventId(eventId, filter, page)
}