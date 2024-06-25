package org.breizhcamp.konter.domain.use_cases

import jakarta.transaction.Transactional
import org.breizhcamp.konter.domain.entities.Talk
import org.breizhcamp.konter.domain.entities.exceptions.EventNoBeginException
import org.breizhcamp.konter.domain.use_cases.ports.EventPort
import org.breizhcamp.konter.domain.use_cases.ports.KalonPort
import org.springframework.stereotype.Service

@Service
class GetTalks(
    private val eventPort: EventPort,
    private val kalonPort: KalonPort
) {
    @Throws
    @Transactional
    fun list(eventId: Int): List<Talk> {
        var event = eventPort.getById(eventId)
        if (event.begin == null) {
            eventPort.save(kalonPort.getEvents())
            event = eventPort.getById(eventId)
            if (event.begin == null) {
                throw EventNoBeginException("No beginning date found for Event:$eventId")
            }
        }
        return eventPort.exportTalks(eventId)
    }
}