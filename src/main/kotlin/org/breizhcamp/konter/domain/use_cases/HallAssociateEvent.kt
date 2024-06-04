package org.breizhcamp.konter.domain.use_cases

import org.breizhcamp.konter.domain.entities.Hall
import org.breizhcamp.konter.domain.use_cases.ports.HallPort
import org.springframework.stereotype.Service

@Service
class HallAssociateEvent(
    private val hallPort: HallPort
) {
    fun associate(id: Int, eventId: Int): Hall = hallPort.associateToEvent(id, eventId)
    fun dissociate(id: Int, eventId: Int): Hall = hallPort.dissociateFromEvent(id, eventId)
}