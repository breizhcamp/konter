package org.breizhcamp.konter.domain.use_cases

import org.breizhcamp.konter.domain.entities.Session
import org.breizhcamp.konter.domain.entities.SessionFilter
import org.breizhcamp.konter.domain.use_cases.ports.SessionPort
import org.springframework.stereotype.Service

@Service
class SessionList(
    private val sessionPort: SessionPort
) {
    fun list(year: Int): List<Session> = sessionPort.getAllByEventYear(year)
    fun filter(year: Int, filter: SessionFilter): List<Session> = sessionPort.filterByEventYear(year, filter)
}