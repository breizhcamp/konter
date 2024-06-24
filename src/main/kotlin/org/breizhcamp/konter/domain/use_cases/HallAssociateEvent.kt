package org.breizhcamp.konter.domain.use_cases

import org.breizhcamp.konter.domain.entities.Hall
import org.breizhcamp.konter.domain.entities.exceptions.HallNotFoundException
import org.breizhcamp.konter.domain.use_cases.ports.EventPort
import org.breizhcamp.konter.domain.use_cases.ports.HallPort
import org.breizhcamp.konter.domain.use_cases.ports.KalonPort
import org.springframework.stereotype.Service

@Service
class HallAssociateEvent(
    private val hallPort: HallPort,
    private val eventPort: EventPort,
    private val kalonPort: KalonPort,
) {
    @Throws
    fun associate(id: Int, eventId: Int, order: Int): Hall {
        if (!eventPort.existsById(eventId)) {
            eventPort.save(kalonPort.getEvents())
            if (!eventPort.existsById(eventId)) {
                throw HallNotFoundException("No Event with id $eventId found")
            }
        }

        return hallPort.associateToEvent(id, eventId, order)
    }
    fun dissociate(id: Int, eventId: Int): Hall = hallPort.dissociateFromEvent(id, eventId)
}